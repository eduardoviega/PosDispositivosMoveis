import { useRouter } from 'expo-router';
import { useState } from 'react';
import { View } from 'react-native';

import {
  Aviso,
  Botao,
  Campo,
  Cartao,
  Rotulo,
  Selecao,
  Tela,
  Texto,
} from '@/components/ui';
import { ROTULO_CATEGORIA_MATERIAL, ROTULO_DIFICULDADE } from '@/domain/regras';
import {
  CATEGORIAS_MATERIAL,
  CategoriaMaterial,
  Ideia,
  Unidade,
  UNIDADES,
} from '@/domain/types';
import { lerNumero } from '@/lib/dinheiro';
import { gerarIdeias } from '@/service/ai/ideias';
import { esp, useCores } from '@/theme';

export default function TelaIdeias() {
  const router = useRouter();
  const c = useCores();

  const [nome, setNome] = useState('');
  const [categoria, setCategoria] = useState<CategoriaMaterial>('fio');
  const [cor, setCor] = useState('');
  const [quantidade, setQuantidade] = useState('');
  const [unidade, setUnidade] = useState<Unidade>('g');
  const [resultado, setResultado] = useState<{ ideias: Ideia[]; aviso?: string } | null>(
    null,
  );
  const [gerando, setGerando] = useState(false);

  const qtd = lerNumero(quantidade);
  const valido = nome.trim() !== '' && qtd !== null && qtd > 0;

  async function pedir() {
    if (!valido || qtd === null) return;
    setGerando(true);
    try {
      const r = await gerarIdeias({
        nome: nome.trim(),
        categoria,
        cor: cor.trim() || undefined,
        quantidade: qtd,
        unidade,
      });
      setResultado({ ideias: r.dados, aviso: r.aviso });
    } finally {
      setGerando(false);
    }
  }

  return (
    <Tela>
      <Cartao>
        <Rotulo>Sobrou material?</Rotulo>
        <Texto variante="pequeno" cor="tinta2">
          Diga o que você tem e quanto, e o app sugere o que dá para fazer — com tempo e
          rendimento estimados.
        </Texto>

        <Campo
          rotulo="Material"
          valor={nome}
          aoMudar={setNome}
          dica="Barbante nº 6 cru"
        />

        <Selecao
          rotulo="Tipo"
          opcoes={CATEGORIAS_MATERIAL}
          valor={categoria}
          aoMudar={setCategoria}
          formatar={(v) => ROTULO_CATEGORIA_MATERIAL[v]}
        />

        <Campo rotulo="Cor" valor={cor} aoMudar={setCor} dica="opcional" />

        <View style={{ flexDirection: 'row', gap: esp.md, alignItems: 'flex-end' }}>
          <View style={{ flex: 1 }}>
            <Campo
              rotulo="Quantidade"
              valor={quantidade}
              aoMudar={setQuantidade}
              teclado="decimal-pad"
              dica="400"
            />
          </View>
        </View>

        <Selecao rotulo="Unidade" opcoes={UNIDADES} valor={unidade} aoMudar={setUnidade} />

        <Botao
          titulo={resultado ? 'Sugerir de novo' : 'Sugerir ideias'}
          aoTocar={pedir}
          carregando={gerando}
          desabilitado={!valido}
        />
      </Cartao>

      {resultado?.aviso ? <Aviso tom="neutro">{resultado.aviso}</Aviso> : null}

      {resultado?.ideias.map((ideia) => (
        <Cartao key={ideia.peca}>
          <Texto variante="secao">{ideia.peca}</Texto>
          <Texto variante="pequeno" cor="tinta2">
            {ideia.horasEstimadas.toString().replace('.', ',')} h ·{' '}
            {ROTULO_DIFICULDADE[ideia.dificuldade]}
            {ideia.rende ? ` · ${ideia.rende}` : ''}
          </Texto>

          {ideia.materiaisExtra.length > 0 ? (
            <Texto variante="pequeno" cor="tinta3">
              Precisa também de: {ideia.materiaisExtra.join(', ')}
            </Texto>
          ) : null}

          <Botao
            titulo="Criar peça a partir desta ideia"
            variante="secundario"
            aoTocar={() =>
              router.push({
                pathname: '/projeto/[id]',
                params: { id: 'novo' },
              })
            }
          />
        </Cartao>
      ))}

      {resultado ? (
        <Texto variante="pequeno" cor="tinta3">
          Tempo e rendimento são estimativas — confira contra a sua própria experiência
          antes de prometer prazo.
        </Texto>
      ) : (
        <View style={{ height: esp.xl, backgroundColor: c.fundo }} />
      )}
    </Tela>
  );
}
