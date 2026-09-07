import { Image } from 'expo-image';
import { useGlobalSearchParams, useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { Alert, Pressable, View } from 'react-native';

import {
  Aviso,
  Botao,
  Campo,
  Cartao,
  EstadoVazio,
  LinhaValor,
  Rotulo,
  Selecao,
  Separador,
  Tela,
  Texto,
} from '@/components/ui';
import { ROTULO_CATEGORIA_PROJETO, ROTULO_DIFICULDADE } from '@/domain/regras';
import {
  CategoriaProjeto,
  CATEGORIAS_PROJETO,
  Consumo,
  custoUnitario,
  Dificuldade,
  DIFICULDADES,
  Projeto,
  STATUS_PROJETO,
  StatusProjeto,
  Unidade,
} from '@/domain/types';
import { converter, unidadesCompativeis } from '@/domain/unidades';
import { escreverNumero, formatarReais, lerNumero } from '@/lib/dinheiro';
import { escolherDaGaleria, paraFonteImagem, tirarFoto } from '@/lib/foto';
import { custoLinha } from '@/service/pricing';
import { novoId } from '@/service/storage';
import { useDados } from '@/state/dados';
import { esp, raio, useCores } from '@/theme';

const ROTULO_STATUS_TELA: Record<StatusProjeto, string> = {
  rascunho: 'Rascunho',
  precificado: 'Precificado',
  vendido: 'Vendido',
};

export default function TelaProjeto() {
  const { id } = useGlobalSearchParams<{ id: string }>();
  const router = useRouter();
  const c = useCores();
  const { projetoPorId, salvarProjeto, excluirProjeto, materiaisAtivos, carregarFotoCheia } =
    useDados();

  const novo = id === 'novo';
  const existente = novo ? undefined : projetoPorId(id);

  const [nome, setNome] = useState(existente?.nome ?? '');
  const [horas, setHoras] = useState(escreverNumero(existente?.horas));
  const [categoria, setCategoria] = useState<CategoriaProjeto>(
    existente?.categoria ?? 'amigurumi',
  );
  const [dificuldade, setDificuldade] = useState<Dificuldade>(
    existente?.dificuldade ?? 'media',
  );
  const [status, setStatus] = useState<StatusProjeto>(existente?.status ?? 'rascunho');
  const [descricao, setDescricao] = useState(existente?.descricao ?? '');
  const [fotoThumb, setFotoThumb] = useState(existente?.fotoThumb);
  const [fotoCheia, setFotoCheia] = useState(existente?.fotoCheia);
  const [consumos, setConsumos] = useState<Consumo[]>(existente?.consumos ?? []);
  const [processandoFoto, setProcessandoFoto] = useState(false);

  // A lista de projetos não traz a foto cheia: ela some do doc
  // principal e mora à parte. Se a peça já tem miniatura mas ainda não temos
  // a foto cheia em memória, busca sob demanda ao abrir a tela.
  useEffect(() => {
    if (!existente?.id || !existente.fotoThumb || fotoCheia) return;
    let ativo = true;
    carregarFotoCheia(existente.id).then((base64) => {
      if (ativo && base64) setFotoCheia(base64);
    });
    return () => {
      ativo = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [existente?.id]);

  // Formulário de nova linha de consumo.
  const [materialId, setMaterialId] = useState<string>(materiaisAtivos[0]?.id ?? '');
  const materialEscolhido = materiaisAtivos.find((m) => m.id === materialId);
  const [unidadeLancamento, setUnidadeLancamento] = useState<Unidade>(
    materialEscolhido?.unidade ?? 'g',
  );
  const [quantidade, setQuantidade] = useState('');

  const horasNum = lerNumero(horas);
  const nomeValido = nome.trim() !== '';
  const horasValidas = horasNum !== null && horasNum > 0;

  function selecionarMaterial(novoId: string) {
    setMaterialId(novoId);
    const m = materiaisAtivos.find((x) => x.id === novoId);
    if (m) setUnidadeLancamento(m.unidade);
  }

  function adicionarConsumo() {
    const qtd = lerNumero(quantidade);
    if (!materialEscolhido || qtd === null || qtd <= 0) return;

    // Converte dentro da família e guarda na unidade do material.
    const qtdNaUnidadeDoMaterial = converter(
      qtd,
      unidadeLancamento,
      materialEscolhido.unidade,
    );

    const linha: Consumo = {
      id: novoId(),
      materialId: materialEscolhido.id,
      materialNome: materialEscolhido.nome,
      unidade: materialEscolhido.unidade,
      quantidade: qtdNaUnidadeDoMaterial,
      // Congela o custo do dia do lançamento.
      custoUnitarioSnapshot: custoUnitario(materialEscolhido),
    };

    setConsumos((atual) => [...atual, linha]);
    setQuantidade('');
  }

  function removerConsumo(idLinha: string) {
    setConsumos((atual) => atual.filter((x) => x.id !== idLinha));
  }

  async function usarFoto(origem: 'camera' | 'galeria') {
    setProcessandoFoto(true);
    try {
      const r = origem === 'camera' ? await tirarFoto() : await escolherDaGaleria();
      if (r) {
        setFotoThumb(r.thumb);
        setFotoCheia(r.cheia);
      }
    } catch {
      Alert.alert('Foto', 'Não foi possível processar a imagem. Tente outra foto.');
    } finally {
      setProcessandoFoto(false);
    }
  }

  async function salvar(): Promise<string | null> {
    if (!nomeValido || !horasValidas || horasNum === null) return null;

    const projeto: Projeto = {
      id: existente?.id ?? novoId(),
      nome: nome.trim(),
      descricao: descricao.trim() || undefined,
      horas: horasNum,
      categoria,
      dificuldade,
      status,
      fotoThumb,
      fotoCheia,
      consumos,
      precificacao: existente?.precificacao,
      criadoEm: existente?.criadoEm ?? new Date().toISOString(),
    };

    await salvarProjeto(projeto);
    return projeto.id;
  }

  async function salvarEVoltar() {
    const idSalvo = await salvar();
    if (idSalvo) router.back();
  }

  async function salvarECalcular() {
    const idSalvo = await salvar();
    if (!idSalvo) return;
    if (novo) {
      router.replace({ pathname: '/projeto/[id]/preco', params: { id: idSalvo } });
    } else {
      router.push({ pathname: '/projeto/[id]/preco', params: { id: idSalvo } });
    }
  }

  function confirmarExclusao() {
    if (!existente) return;
    Alert.alert('Excluir peça', `“${existente.nome}” será apagada. Não dá para desfazer.`, [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Excluir',
        style: 'destructive',
        onPress: async () => {
          await excluirProjeto(existente.id);
          router.dismissTo('/');
        },
      },
    ]);
  }

  const custoMaterialParcial = consumos.reduce((s, x) => s + custoLinha(x), 0);

  return (
    <Tela>
      <Campo
        rotulo="Nome da peça"
        valor={nome}
        aoMudar={setNome}
        dica="Coelho amigurumi"
        erro={nome !== '' && !nomeValido ? 'Dê um nome à peça.' : undefined}
      />

      <Campo
        rotulo="Horas de trabalho"
        valor={horas}
        aoMudar={setHoras}
        teclado="decimal-pad"
        sufixo="h"
        dica="6"
        erro={horas !== '' && !horasValidas ? 'As horas precisam ser maiores que zero.' : undefined}
      />

      <Selecao
        rotulo="Categoria"
        opcoes={CATEGORIAS_PROJETO}
        valor={categoria}
        aoMudar={setCategoria}
        formatar={(v) => ROTULO_CATEGORIA_PROJETO[v]}
      />

      <Selecao
        rotulo="Dificuldade"
        opcoes={DIFICULDADES}
        valor={dificuldade}
        aoMudar={setDificuldade}
        formatar={(v) => ROTULO_DIFICULDADE[v]}
      />

      <Selecao
        rotulo="Situação"
        opcoes={STATUS_PROJETO}
        valor={status}
        aoMudar={setStatus}
        formatar={(v) => ROTULO_STATUS_TELA[v]}
      />

      <Campo
        rotulo="Anotações"
        valor={descricao}
        aoMudar={setDescricao}
        dica="Ponto usado, tamanho, o que faria diferente..."
        multilinha
      />

      {/* ------------------------------------------------------------ foto */}
      <Cartao>
        <Rotulo>Foto</Rotulo>
        {fotoThumb ? (
          <Image
            source={paraFonteImagem(fotoCheia ?? fotoThumb)}
            style={{ width: '100%', height: 220, borderRadius: raio.md }}
            contentFit="cover"
          />
        ) : (
          <Texto variante="pequeno" cor="tinta2">
            A foto é comprimida no aparelho antes de ser guardada, para caber no limite de
            1 MiB por documento.
          </Texto>
        )}
        <View style={{ flexDirection: 'row', gap: esp.md }}>
          <View style={{ flex: 1 }}>
            <Botao
              titulo="Câmera"
              variante="secundario"
              carregando={processandoFoto}
              aoTocar={() => usarFoto('camera')}
            />
          </View>
          <View style={{ flex: 1 }}>
            <Botao
              titulo="Galeria"
              variante="secundario"
              carregando={processandoFoto}
              aoTocar={() => usarFoto('galeria')}
            />
          </View>
        </View>
      </Cartao>

      {/* -------------------------------------------------------- consumos */}
      <Cartao>
        <Rotulo>Materiais usados</Rotulo>

        {consumos.length === 0 ? (
          <Texto variante="pequeno" cor="tinta2">
            Sem material lançado, o preço sai só da mão de obra.
          </Texto>
        ) : (
          <View style={{ gap: esp.sm }}>
            {consumos.map((linha) => (
              <View key={linha.id}>
                <View
                  style={{
                    flexDirection: 'row',
                    alignItems: 'center',
                    gap: esp.md,
                  }}>
                  <View style={{ flex: 1 }}>
                    <Texto numeroDeLinhas={1}>{linha.materialNome}</Texto>
                    <Texto variante="pequeno" cor="tinta3">
                      {linha.quantidade.toString().replace('.', ',')} {linha.unidade}
                    </Texto>
                  </View>
                  <Texto variante="numero">{formatarReais(custoLinha(linha))}</Texto>
                  <Pressable
                    onPress={() => removerConsumo(linha.id)}
                    accessibilityRole="button"
                    accessibilityLabel={`Remover ${linha.materialNome}`}
                    hitSlop={8}>
                    <Texto cor="erro" variante="numero">
                      ×
                    </Texto>
                  </Pressable>
                </View>
                <Separador />
              </View>
            ))}
            <LinhaValor
              rotulo="Custo de material"
              valor={formatarReais(custoMaterialParcial)}
              destaque
            />
          </View>
        )}

        {materiaisAtivos.length === 0 ? (
          <EstadoVazio
            titulo="Nenhum material no catálogo"
            descricao="Cadastre um fio na aba Materiais para poder lançar o consumo aqui."
            acao={{
              titulo: 'Ir para Materiais',
              aoTocar: () => router.push('/materiais'),
            }}
          />
        ) : (
          <View style={{ gap: esp.md, marginTop: esp.sm }}>
            <Selecao
              rotulo="Adicionar material"
              opcoes={materiaisAtivos.map((m) => m.id)}
              valor={materialId}
              aoMudar={selecionarMaterial}
              formatar={(v) => materiaisAtivos.find((m) => m.id === v)?.nome ?? v}
            />

            {materialEscolhido ? (
              <>
                {unidadesCompativeis(materialEscolhido.unidade).length > 1 ? (
                  <Selecao
                    rotulo="Unidade do lançamento"
                    opcoes={unidadesCompativeis(materialEscolhido.unidade)}
                    valor={unidadeLancamento}
                    aoMudar={setUnidadeLancamento}
                  />
                ) : null}

                <Campo
                  rotulo="Quantidade usada"
                  valor={quantidade}
                  aoMudar={setQuantidade}
                  teclado="decimal-pad"
                  sufixo={unidadeLancamento}
                  dica="100"
                />

                <Botao
                  titulo="Adicionar à peça"
                  variante="secundario"
                  aoTocar={adicionarConsumo}
                  desabilitado={(lerNumero(quantidade) ?? 0) <= 0}
                />
              </>
            ) : null}
          </View>
        )}
      </Cartao>

      {existente?.precificacao ? (
        <Aviso tom="neutro">
          Esta peça já tem preço gravado de{' '}
          {formatarReais(existente.precificacao.precoFinal)}. Recalcular sobrescreve o
          valor anterior — não há histórico de preço.
        </Aviso>
      ) : null}

      <Botao
        titulo="Salvar e calcular preço"
        aoTocar={salvarECalcular}
        desabilitado={!nomeValido || !horasValidas}
      />
      <Botao
        titulo="Só salvar"
        variante="secundario"
        aoTocar={salvarEVoltar}
        desabilitado={!nomeValido || !horasValidas}
      />
      {existente ? (
        <Botao titulo="Excluir peça" variante="perigo" aoTocar={confirmarExclusao} />
      ) : null}

      <View style={{ height: esp.xl, backgroundColor: c.fundo }} />
    </Tela>
  );
}
