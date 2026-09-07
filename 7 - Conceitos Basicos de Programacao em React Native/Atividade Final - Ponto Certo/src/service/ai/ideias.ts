/**
 * IA-03 — o que fazer com o material que sobrou.
 *
 * Entra material e quantidade, saem de 3 a 5 ideias estruturadas, para poder
 * renderizar em cartões e criar um projeto a partir da escolhida.
 */

import { CategoriaMaterial, Ideia, ResultadoIA, Unidade } from '@/domain/types';
import { converter } from '@/domain/unidades';
import { pedirJson } from './cliente';
import { RespostaInvalidaError } from './validacao';

export type PedidoIdeias = {
  nome: string;
  categoria: CategoriaMaterial;
  cor?: string;
  quantidade: number;
  unidade: Unidade;
};

const SISTEMA = [
  'Você sugere o que fazer com uma quantidade de material de crochê.',
  'Devolva de 3 a 5 ideias realistas para a quantidade informada.',
  'Responda só JSON, no formato {"ideias": [{"peca": string, "categoria": string,',
  '"horas_estimadas": number, "dificuldade": "facil"|"media"|"dificil",',
  '"rende": string, "materiais_extra": string[]}]}.',
].join(' ');

/** Fallback local: tabela por faixa de quantidade, em gramas de fio. */
const TABELA_FIO: { ate: number; ideias: Ideia[] }[] = [
  {
    ate: 120,
    ideias: [
      {
        peca: 'Chaveiro de amigurumi',
        categoria: 'acessorio',
        horasEstimadas: 2,
        dificuldade: 'facil',
        rende: '2 a 3 chaveiros pequenos',
        materiaisExtra: ['argola de chaveiro', 'enchimento'],
      },
      {
        peca: 'Porta-copos em ponto baixo',
        categoria: 'decoracao',
        horasEstimadas: 1.5,
        dificuldade: 'facil',
        rende: 'jogo de 4 porta-copos',
        materiaisExtra: [],
      },
      {
        peca: 'Flores para aplicação',
        categoria: 'decoracao',
        horasEstimadas: 1,
        dificuldade: 'facil',
        rende: '6 a 8 flores pequenas',
        materiaisExtra: [],
      },
    ],
  },
  {
    ate: 400,
    ideias: [
      {
        peca: 'Amigurumi de porte médio',
        categoria: 'amigurumi',
        horasEstimadas: 6,
        dificuldade: 'media',
        rende: '1 boneco de 20 a 25 cm',
        materiaisExtra: ['fibra siliconada', 'olhos de segurança'],
      },
      {
        peca: 'Necessaire de crochê',
        categoria: 'acessorio',
        horasEstimadas: 5,
        dificuldade: 'media',
        rende: '1 necessaire de 20 × 12 cm',
        materiaisExtra: ['zíper de 20 cm', 'forro de tecido'],
      },
      {
        peca: 'Cesto organizador',
        categoria: 'decoracao',
        horasEstimadas: 4,
        dificuldade: 'facil',
        rende: '1 cesto de 18 cm de diâmetro',
        materiaisExtra: [],
      },
    ],
  },
  {
    ate: Number.POSITIVE_INFINITY,
    ideias: [
      {
        peca: 'Bolsa de praia com alça trançada',
        categoria: 'bolsa',
        horasEstimadas: 9,
        dificuldade: 'media',
        rende: '1 bolsa de 30 × 35 cm',
        materiaisExtra: ['forro de tecido', 'zíper de 30 cm'],
      },
      {
        peca: 'Tapete oval de sala',
        categoria: 'tapete',
        horasEstimadas: 12,
        dificuldade: 'media',
        rende: '1 tapete de 60 × 90 cm',
        materiaisExtra: ['antiderrapante'],
      },
      {
        peca: 'Manta ou xale',
        categoria: 'roupa',
        horasEstimadas: 16,
        dificuldade: 'dificil',
        rende: '1 manta de 100 × 120 cm',
        materiaisExtra: [],
      },
    ],
  },
];

const IDEIAS_GENERICAS: Ideia[] = [
  {
    peca: 'Aplicação para outra peça',
    categoria: 'acessorio',
    horasEstimadas: 1,
    dificuldade: 'facil',
    rende: 'depende da quantidade disponível',
    materiaisExtra: [],
  },
  {
    peca: 'Complemento de um projeto em andamento',
    categoria: 'outro',
    horasEstimadas: 2,
    dificuldade: 'facil',
    rende: 'usa a sobra sem desperdício',
    materiaisExtra: [],
  },
  {
    peca: 'Amostra de ponto para o catálogo',
    categoria: 'outro',
    horasEstimadas: 1,
    dificuldade: 'facil',
    rende: '2 a 3 amostras',
    materiaisExtra: [],
  },
];

export function ideiasLocais(pedido: PedidoIdeias): Ideia[] {
  if (pedido.categoria !== 'fio') return IDEIAS_GENERICAS;

  let gramas: number;
  try {
    gramas = converter(pedido.quantidade, pedido.unidade, 'g');
  } catch {
    // Fio medido em metros: sem tabela confiável, cai no genérico.
    return IDEIAS_GENERICAS;
  }

  const faixa = TABELA_FIO.find((f) => gramas <= f.ate);
  return faixa ? faixa.ideias : IDEIAS_GENERICAS;
}

function lerIdeia(cru: unknown): Ideia {
  if (typeof cru !== 'object' || cru === null) {
    throw new RespostaInvalidaError('ideia não é objeto');
  }
  const o = cru as Record<string, unknown>;
  if (typeof o.peca !== 'string' || typeof o.horas_estimadas !== 'number') {
    throw new RespostaInvalidaError('ideia sem peca ou horas_estimadas');
  }
  return {
    peca: o.peca,
    categoria: (o.categoria as Ideia['categoria']) ?? 'outro',
    horasEstimadas: o.horas_estimadas,
    dificuldade: (o.dificuldade as Ideia['dificuldade']) ?? 'media',
    rende: typeof o.rende === 'string' ? o.rende : '',
    materiaisExtra: Array.isArray(o.materiais_extra)
      ? (o.materiais_extra.filter((m) => typeof m === 'string') as string[])
      : [],
  };
}

export async function gerarIdeias(pedido: PedidoIdeias): Promise<ResultadoIA<Ideia[]>> {
  const usuario = [
    `Material: ${pedido.nome}${pedido.cor ? ` na cor ${pedido.cor}` : ''}`,
    `Tipo: ${pedido.categoria}`,
    `Quantidade disponível: ${pedido.quantidade} ${pedido.unidade}`,
  ].join('\n');

  try {
    const cru = await pedirJson<Record<string, unknown>>(SISTEMA, usuario);
    if (!Array.isArray(cru.ideias) || cru.ideias.length === 0) {
      throw new RespostaInvalidaError('lista de ideias vazia');
    }
    return { dados: cru.ideias.slice(0, 5).map(lerIdeia), origem: 'ia' };
  } catch {
    return {
      dados: ideiasLocais(pedido),
      origem: 'fallback',
      aviso: 'Sugestões da tabela do aparelho — a IA não respondeu.',
    };
  }
}
