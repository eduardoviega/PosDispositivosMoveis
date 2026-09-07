import * as Clipboard from 'expo-clipboard';
import { useGlobalSearchParams, useRouter } from 'expo-router';
import { useMemo, useState } from 'react';
import { View } from 'react-native';

import { BarraComposicao } from '@/components/barra-composicao';
import {
  Aviso,
  Botao,
  Campo,
  Cartao,
  Etiqueta,
  LinhaValor,
  Rotulo,
  Tela,
  Texto,
} from '@/components/ui';
import { custoDesatualizado } from '@/domain/regras';
import { DefesaPreco } from '@/domain/types';
import {
  escreverCentavos,
  formatarPercentual,
  formatarReais,
  lerCentavos,
} from '@/lib/dinheiro';
import { gerarDefesa } from '@/service/ai/defesa';
import { avisos, calcularPrecificacao } from '@/service/pricing';
import { useDados } from '@/state/dados';
import { esp, useCores } from '@/theme';

export default function TelaPreco() {
  const { id } = useGlobalSearchParams<{ id: string }>();
  const router = useRouter();
  const c = useCores();
  const { projetoPorId, perfil, materiais, salvarProjeto } = useDados();

  const projeto = projetoPorId(id);
  const [precoEditado, setPrecoEditado] = useState<string | null>(null);
  const [defesa, setDefesa] = useState<{ dados: DefesaPreco; aviso?: string } | null>(null);
  const [gerandoDefesa, setGerandoDefesa] = useState(false);
  const [copiado, setCopiado] = useState(false);

  // Recalcula em tempo real enquanto a tela está aberta; só grava
  // quando a artesã confirma.
  const precoFinalManual = precoEditado === null ? undefined : lerCentavos(precoEditado);

  const p = useMemo(
    () =>
      calcularPrecificacao({
        horas: projeto?.horas ?? 0,
        consumos: projeto?.consumos ?? [],
        perfil,
        precoFinal: precoFinalManual ?? undefined,
      }),
    [projeto?.horas, projeto?.consumos, perfil, precoFinalManual],
  );

  if (!projeto) {
    return (
      <Tela>
        <Texto>Esta peça não existe mais.</Texto>
        <Botao titulo="Voltar" variante="secundario" aoTocar={() => router.back()} />
      </Tela>
    );
  }

  // Capturado depois da guarda: `function` não herda o estreitamento de tipo.
  const peca = projeto;
  const listaAvisos = avisos(p, peca.consumos.length);
  const desatualizado = custoDesatualizado(peca, materiais);

  async function confirmar() {
    await salvarProjeto({
      ...peca,
      status: peca.status === 'rascunho' ? 'precificado' : peca.status,
      precificacao: p,
    });
    router.back();
  }

  async function pedirDefesa() {
    setGerandoDefesa(true);
    try {
      const r = await gerarDefesa(peca, p);
      setDefesa({ dados: r.dados, aviso: r.aviso });
    } finally {
      setGerandoDefesa(false);
    }
  }

  async function copiarDefesa() {
    if (!defesa) return;
    await Clipboard.setStringAsync(defesa.dados.resposta);
    setCopiado(true);
    setTimeout(() => setCopiado(false), 2000);
  }

  return (
    <Tela>
      {/* --------------------------------------------------- preço sugerido */}
      <Cartao>
        <Rotulo>Preço sugerido</Rotulo>
        <Texto variante="destaque" cor="acento">
          {formatarReais(p.precoSugerido)}
        </Texto>
        <Texto variante="pequeno" cor="tinta2">
          Piso de venda (custo total): {formatarReais(p.custoTotal)}. Abaixo disso, você
          paga para trabalhar.
        </Texto>
        {desatualizado ? (
          <Etiqueta tom="atencao">custo desatualizado — recalcule</Etiqueta>
        ) : null}
      </Cartao>

      {/* ------------------------------------------------------ detalhamento */}
      <Cartao>
        <Rotulo>Como esse número aparece</Rotulo>

        <LinhaValor rotulo="Material" valor={formatarReais(p.custoMaterial)} />
        <LinhaValor
          rotulo={`Mão de obra · ${projeto.horas.toString().replace('.', ',')} h × ${formatarReais(perfil.valorHora)}`}
          valor={formatarReais(p.custoMaoObra)}
        />
        <LinhaValor
          rotulo={`Indiretos · ${formatarPercentual(perfil.percIndiretos)}`}
          valor={formatarReais(p.custoIndiretos)}
        />
        <LinhaValor rotulo="Custo total" valor={formatarReais(p.custoTotal)} destaque />
        <LinhaValor
          rotulo={`Lucro · ${formatarPercentual(perfil.percLucro)}`}
          valor={formatarReais(p.precoBase - p.custoTotal)}
        />
        <LinhaValor
          rotulo={`Taxas de venda · ${formatarPercentual(perfil.percTaxas)}`}
          valor={formatarReais(p.precoComTaxa - p.precoBase)}
        />
        <LinhaValor
          rotulo={`Arredondamento · ${perfil.arredondamento === 0 ? 'nenhum' : formatarReais(perfil.arredondamento)}`}
          valor={formatarReais(p.precoSugerido - p.precoComTaxa)}
        />
        <LinhaValor rotulo="Preço sugerido" valor={formatarReais(p.precoSugerido)} destaque />

        <Texto variante="pequeno" cor="tinta3">
          A taxa entra por divisão, não por soma: somar {formatarPercentual(perfil.percTaxas)}{' '}
          ao preço-base deixaria você no prejuízo depois do desconto da maquininha.
        </Texto>
      </Cartao>

      {/* ---------------------------------------------------------- barra */}
      <Cartao>
        <BarraComposicao p={p} />
      </Cartao>

      {/* ------------------------------------------------------ preço final */}
      <Cartao>
        <Rotulo>Quanto você vai cobrar</Rotulo>
        <Campo
          rotulo="Preço final"
          valor={precoEditado ?? escreverCentavos(p.precoSugerido)}
          aoMudar={setPrecoEditado}
          teclado="decimal-pad"
          sufixo="R$"
        />

        {listaAvisos.map((a) => (
          <Aviso key={a.texto} tom={a.tipo === 'erro' ? 'erro' : 'atencao'}>
            {a.texto}
          </Aviso>
        ))}

        <LinhaValor
          rotulo="Margem real"
          valor={formatarPercentual(p.margemReal)}
          cor={p.margemReal < 0 ? 'erro' : undefined}
        />
        <LinhaValor
          rotulo="Retorno por hora"
          valor={`${formatarReais(p.retornoHora)}/h`}
          destaque
          cor={p.retornoHora < perfil.valorHora ? 'atencao' : 'positivo'}
        />
        <Texto variante="pequeno" cor="tinta3">
          Retorno por hora é o que você ganha de fato nesta peça, já descontando material,
          indiretos e taxas. É o número que responde “vale a pena fazer outra?”.
        </Texto>

        <Botao titulo="Confirmar este preço" aoTocar={confirmar} />
      </Cartao>

      {/* ----------------------------------------------------- IA-01 defesa */}
      <Cartao>
        <Rotulo>Cliente achou caro?</Rotulo>
        <Texto variante="pequeno" cor="tinta2">
          Gera a resposta com as horas, os materiais e o custo desta peça — só com os
          números que estão aí em cima.
        </Texto>

        {defesa ? (
          <>
            <View
              style={{
                backgroundColor: c.superficieAlt,
                borderRadius: esp.sm,
                padding: esp.md,
                gap: esp.sm,
              }}>
              <Texto>{defesa.dados.resposta}</Texto>
            </View>

            <View style={{ gap: esp.xs }}>
              {defesa.dados.pontos.map((ponto) => (
                <Texto key={ponto} variante="pequeno" cor="tinta2">
                  • {ponto}
                </Texto>
              ))}
            </View>

            {defesa.aviso ? <Aviso tom="neutro">{defesa.aviso}</Aviso> : null}

            <Botao
              titulo={copiado ? 'Copiado' : 'Copiar mensagem'}
              variante="secundario"
              aoTocar={copiarDefesa}
            />
          </>
        ) : (
          <Botao
            titulo="Escrever a resposta"
            variante="secundario"
            carregando={gerandoDefesa}
            aoTocar={pedirDefesa}
          />
        )}
      </Cartao>
    </Tela>
  );
}
