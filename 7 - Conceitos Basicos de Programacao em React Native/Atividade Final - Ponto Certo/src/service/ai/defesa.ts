/**
 * IA-01 — defesa do preço.
 *
 * Recebe o detalhamento já calculado e devolve o texto para responder ao
 * cliente que disse "tá caro". Não estima nada e não opina sobre o valor: só
 * escreve a partir dos números que a fórmula produziu.
 */

import { DefesaPreco, Precificacao, Projeto, ResultadoIA } from '@/domain/types';
import { formatarCentavos, formatarReais } from '@/lib/dinheiro';
import { pedirJson } from './cliente';
import { citaValorInventado, exigirListaDeTexto, exigirTexto } from './validacao';

const SISTEMA = [
  'Você escreve mensagens curtas e gentis que uma artesã manda ao cliente para',
  'explicar o preço de uma peça de crochê feita à mão.',
  'Use apenas os números que receber; nunca calcule nem invente valores.',
  'Não use linguagem agressiva nem defensiva. Responda só JSON, no formato',
  '{"resposta": string, "pontos": string[]}.',
].join(' ');

function horasEmTexto(horas: number): string {
  if (Number.isInteger(horas)) return `${horas} ${horas === 1 ? 'hora' : 'horas'}`;
  return `${horas.toString().replace('.', ',')} horas`;
}

export function defesaLocal(projeto: Projeto, p: Precificacao): DefesaPreco {
  const materiais = projeto.consumos.map((c) => c.materialNome);
  const listaMateriais =
    materiais.length === 0
      ? 'material selecionado'
      : materiais.length === 1
        ? materiais[0]
        : `${materiais.slice(0, -1).join(', ')} e ${materiais[materiais.length - 1]}`;

  const resposta =
    `Essa peça leva ${horasEmTexto(projeto.horas)} de crochê feito à mão, ponto por ponto. ` +
    `São ${listaMateriais}, ${formatarReais(p.custoMaterial)} só de material. ` +
    `O valor de ${formatarReais(p.precoFinal)} cobre o material e o tempo de trabalho.`;

  return {
    resposta,
    pontos: [
      `${horasEmTexto(projeto.horas)} de trabalho manual`,
      `${formatarReais(p.custoMaterial)} em material`,
      'peça única, feita sob encomenda',
    ],
  };
}

export async function gerarDefesa(
  projeto: Projeto,
  p: Precificacao,
): Promise<ResultadoIA<DefesaPreco>> {
  const permitidos = [
    formatarCentavos(p.custoMaterial),
    formatarCentavos(p.custoTotal),
    formatarCentavos(p.precoFinal),
  ];

  const usuario = [
    `Peça: ${projeto.nome} (${projeto.categoria}, dificuldade ${projeto.dificuldade}).`,
    `Horas de trabalho: ${projeto.horas}.`,
    `Materiais: ${projeto.consumos.map((c) => `${c.materialNome} ${c.quantidade}${c.unidade}`).join('; ') || 'nenhum'}.`,
    `Custo de material: R$ ${permitidos[0]}.`,
    `Custo total: R$ ${permitidos[1]}.`,
    `Preço de venda: R$ ${permitidos[2]}.`,
    'Escreva a mensagem para o cliente que achou caro.',
  ].join('\n');

  try {
    const cru = await pedirJson<Record<string, unknown>>(SISTEMA, usuario);
    const resposta = exigirTexto(cru.resposta, 'resposta');
    const pontos = exigirListaDeTexto(cru.pontos, 'pontos');

    if (citaValorInventado([resposta, ...pontos].join(' '), permitidos)) {
      throw new Error('valor não enviado no prompt');
    }

    return { dados: { resposta, pontos }, origem: 'ia' };
  } catch {
    return {
      dados: defesaLocal(projeto, p),
      origem: 'fallback',
      aviso: 'Texto montado no aparelho — a IA não respondeu.',
    };
  }
}
