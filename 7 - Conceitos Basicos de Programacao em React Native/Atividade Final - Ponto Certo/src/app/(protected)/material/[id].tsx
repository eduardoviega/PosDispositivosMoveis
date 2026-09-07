import { useLocalSearchParams, useRouter } from 'expo-router';
import { useMemo, useState } from 'react';
import { Alert, View } from 'react-native';

import {
  Aviso,
  Botao,
  Campo,
  Cartao,
  LinhaValor,
  Rotulo,
  Selecao,
  Tela,
  Texto,
} from '@/components/ui';
import { ROTULO_CATEGORIA_MATERIAL } from '@/domain/regras';
import {
  CATEGORIAS_MATERIAL,
  CategoriaMaterial,
  Material,
  Unidade,
  UNIDADES,
} from '@/domain/types';
import { escreverCentavos, escreverNumero, formatarCentavos, lerCentavos, lerNumero } from '@/lib/dinheiro';
import { novoId } from '@/service/storage';
import { useDados } from '@/state/dados';
import { esp } from '@/theme';

export default function TelaMaterial() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const { materialPorId, salvarMaterial, arquivarMaterial, materialEmUso } = useDados();

  const existente = id === 'novo' ? undefined : materialPorId(id);

  const [nome, setNome] = useState(existente?.nome ?? '');
  const [categoria, setCategoria] = useState<CategoriaMaterial>(
    existente?.categoria ?? 'fio',
  );
  const [marca, setMarca] = useState(existente?.marca ?? '');
  const [cor, setCor] = useState(existente?.cor ?? '');
  const [unidade, setUnidade] = useState<Unidade>(existente?.unidade ?? 'g');
  const [qtd, setQtd] = useState(escreverNumero(existente?.qtdEmbalagem));
  const [preco, setPreco] = useState(escreverCentavos(existente?.precoEmbalagem));

  const qtdNum = lerNumero(qtd);
  const precoCent = lerCentavos(preco);

  const unitario = useMemo(() => {
    if (!qtdNum || qtdNum <= 0 || precoCent === null) return null;
    return precoCent / qtdNum;
  }, [qtdNum, precoCent]);

  const erros = {
    nome: nome.trim() === '' ? 'Dê um nome ao material.' : undefined,
    qtd:
      qtdNum === null || qtdNum <= 0
        ? 'Quanto vem na embalagem? Precisa ser maior que zero.'
        : undefined,
    preco:
      precoCent === null || precoCent <= 0
        ? 'Quanto você pagou pela embalagem?'
        : undefined,
  };
  const valido = !erros.nome && !erros.qtd && !erros.preco;

  async function salvar() {
    if (!valido || qtdNum === null || precoCent === null) return;

    const material: Material = {
      id: existente?.id ?? novoId(),
      nome: nome.trim(),
      categoria,
      marca: marca.trim() || undefined,
      cor: cor.trim() || undefined,
      unidade,
      qtdEmbalagem: qtdNum,
      precoEmbalagem: precoCent,
      arquivado: existente?.arquivado ?? false,
      criadoEm: existente?.criadoEm ?? new Date().toISOString(),
    };

    await salvarMaterial(material);
    router.back();
  }

  function confirmarArquivar() {
    if (!existente) return;
    const emUso = materialEmUso(existente.id);
    Alert.alert(
      'Arquivar material',
      emUso
        ? 'Este material é usado em alguma peça, então ele não pode ser excluído — ' +
            'só arquivado. Ele continua aparecendo nos projetos antigos.'
        : 'O material sai da lista de escolha, mas continua nos projetos antigos.',
      [
        { text: 'Cancelar', style: 'cancel' },
        {
          text: 'Arquivar',
          style: 'destructive',
          onPress: async () => {
            await arquivarMaterial(existente.id);
            router.back();
          },
        },
      ],
    );
  }

  return (
    <Tela>
      <Campo
        rotulo="Nome"
        valor={nome}
        aoMudar={setNome}
        dica="Barbante Barroco nº 6 cru"
        erro={erros.nome}
      />

      <Selecao
        rotulo="Categoria"
        opcoes={CATEGORIAS_MATERIAL}
        valor={categoria}
        aoMudar={setCategoria}
        formatar={(v) => ROTULO_CATEGORIA_MATERIAL[v]}
      />

      <View style={{ flexDirection: 'row', gap: esp.md }}>
        <View style={{ flex: 1 }}>
          <Campo rotulo="Marca" valor={marca} aoMudar={setMarca} dica="opcional" />
        </View>
        <View style={{ flex: 1 }}>
          <Campo rotulo="Cor" valor={cor} aoMudar={setCor} dica="opcional" />
        </View>
      </View>

      <Selecao
        rotulo="Unidade de medida"
        opcoes={UNIDADES}
        valor={unidade}
        aoMudar={setUnidade}
      />

      <Cartao>
        <Rotulo>Embalagem</Rotulo>
        <Texto variante="pequeno" cor="tinta2">
          O custo por {unidade} é calculado, nunca digitado.
        </Texto>

        <View style={{ flexDirection: 'row', gap: esp.md }}>
          <View style={{ flex: 1 }}>
            <Campo
              rotulo="Quantidade"
              valor={qtd}
              aoMudar={setQtd}
              teclado="decimal-pad"
              sufixo={unidade}
              dica="100"
              erro={erros.qtd}
            />
          </View>
          <View style={{ flex: 1 }}>
            <Campo
              rotulo="Preço pago"
              valor={preco}
              aoMudar={setPreco}
              teclado="decimal-pad"
              sufixo="R$"
              dica="18,90"
              erro={erros.preco}
            />
          </View>
        </View>

        {unitario !== null ? (
          <LinhaValor
            rotulo={`Custo por ${unidade}`}
            valor={`R$ ${formatarCentavos(unitario)}`}
            destaque
          />
        ) : null}
      </Cartao>

      {existente && materialEmUso(existente.id) ? (
        <Aviso tom="neutro">
          Mudar o preço aqui não altera as peças já precificadas: elas continuam com o
          custo do dia em que foram calculadas e passam a exibir “custo desatualizado”.
        </Aviso>
      ) : null}

      <Botao titulo="Salvar material" aoTocar={salvar} desabilitado={!valido} />

      {existente && !existente.arquivado ? (
        <Botao titulo="Arquivar" variante="perigo" aoTocar={confirmarArquivar} />
      ) : null}
    </Tela>
  );
}
