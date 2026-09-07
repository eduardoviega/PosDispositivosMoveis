/**
 * IA-02 — anúncio de venda. Título, descrição, bullets e hashtags a partir do
 * que já está cadastrado no projeto.
 */

import { Anuncio, Projeto, ResultadoIA } from '@/domain/types';
import { pedirJson } from './cliente';
import { exigirListaDeTexto, exigirTexto } from './validacao';

export const TONS = ['fofo', 'profissional', 'minimalista'] as const;
export type Tom = (typeof TONS)[number];

export const CANAIS = ['Instagram', 'Facebook Marketplace', 'Shopee'] as const;
export type Canal = (typeof CANAIS)[number];

const SISTEMA = [
  'Você escreve anúncios de peças de crochê artesanais para venda online.',
  'Seja específico e concreto, sem exagero publicitário e sem prometer o que',
  'não foi informado. Responda só JSON, no formato',
  '{"titulo": string, "descricao": string, "bullets": string[], "hashtags": string[]}.',
].join(' ');

const DESCRICAO_CATEGORIA: Record<string, string> = {
  amigurumi: 'bichinho de crochê feito à mão',
  bolsa: 'bolsa de crochê feita à mão',
  tapete: 'tapete de crochê feito à mão',
  roupa: 'peça de vestuário em crochê feita à mão',
  acessorio: 'acessório de crochê feito à mão',
  decoracao: 'peça de decoração em crochê feita à mão',
  outro: 'peça de crochê feita à mão',
};

export function anuncioLocal(projeto: Projeto, tom: Tom): Anuncio {
  const oQueE = DESCRICAO_CATEGORIA[projeto.categoria] ?? DESCRICAO_CATEGORIA.outro;
  const materiais = projeto.consumos.map((c) => c.materialNome).join(', ');

  const abertura =
    tom === 'fofo'
      ? `${projeto.nome} feito com todo o carinho 🧶`
      : tom === 'minimalista'
        ? projeto.nome
        : `${projeto.nome} — ${oQueE}`;

  const descricao = [
    `${abertura}.`,
    projeto.descricao?.trim() ? projeto.descricao.trim() : `${oQueE.charAt(0).toUpperCase()}${oQueE.slice(1)}.`,
    materiais ? `Feito com ${materiais}.` : '',
    'Peça única, produzida sob encomenda.',
  ]
    .filter(Boolean)
    .join(' ');

  return {
    titulo: abertura,
    descricao,
    bullets: [
      'Feito à mão, ponto por ponto',
      materiais ? `Materiais: ${materiais}` : 'Materiais selecionados',
      'Produção sob encomenda',
    ],
    hashtags: ['#crochê', '#feitoamao', `#${projeto.categoria}`, '#artesanato'],
  };
}

export async function gerarAnuncio(
  projeto: Projeto,
  tom: Tom,
  canal: Canal,
): Promise<ResultadoIA<Anuncio>> {
  const usuario = [
    `Peça: ${projeto.nome}`,
    `Categoria: ${projeto.categoria}`,
    `Dificuldade: ${projeto.dificuldade}`,
    `Materiais: ${projeto.consumos.map((c) => c.materialNome).join(', ') || 'não informados'}`,
    projeto.descricao ? `Anotações da artesã: ${projeto.descricao}` : '',
    `Tom desejado: ${tom}`,
    `Canal de venda: ${canal}`,
  ]
    .filter(Boolean)
    .join('\n');

  try {
    const cru = await pedirJson<Record<string, unknown>>(SISTEMA, usuario);
    return {
      dados: {
        titulo: exigirTexto(cru.titulo, 'titulo'),
        descricao: exigirTexto(cru.descricao, 'descricao'),
        bullets: exigirListaDeTexto(cru.bullets, 'bullets'),
        hashtags: exigirListaDeTexto(cru.hashtags, 'hashtags'),
      },
      origem: 'ia',
    };
  } catch {
    return {
      dados: anuncioLocal(projeto, tom),
      origem: 'fallback',
      aviso: 'Texto montado no aparelho — a IA não respondeu.',
    };
  }
}
