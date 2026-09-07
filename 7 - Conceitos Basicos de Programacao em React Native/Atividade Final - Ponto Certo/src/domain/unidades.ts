/**
 * Unidades de medida — a maior fonte de bug previsível do projeto.
 *
 * O consumo é lançado na mesma família de unidade do material.
 * Conversão só dentro da família (kg↔g, m↔cm). Somar peso com unidade é erro
 * de validação, nunca conversão silenciosa.
 */

import { Unidade } from './types';

export type FamiliaUnidade = 'peso' | 'comprimento' | 'contagem';

const FAMILIA: Record<Unidade, FamiliaUnidade> = {
  g: 'peso',
  kg: 'peso',
  m: 'comprimento',
  cm: 'comprimento',
  un: 'contagem',
};

/** Fator para a unidade base da família (g, cm, un). */
const PARA_BASE: Record<Unidade, number> = {
  g: 1,
  kg: 1000,
  cm: 1,
  m: 100,
  un: 1,
};

export const ROTULO_UNIDADE: Record<Unidade, string> = {
  g: 'gramas',
  kg: 'quilos',
  m: 'metros',
  cm: 'centímetros',
  un: 'unidades',
};

export function familia(unidade: Unidade): FamiliaUnidade {
  return FAMILIA[unidade];
}

export function mesmaFamilia(a: Unidade, b: Unidade): boolean {
  return FAMILIA[a] === FAMILIA[b];
}

/** Unidades que podem ser lançadas para um material desta unidade. */
export function unidadesCompativeis(unidade: Unidade): Unidade[] {
  return (Object.keys(FAMILIA) as Unidade[]).filter((u) => mesmaFamilia(u, unidade));
}

export class UnidadeIncompativelError extends Error {
  constructor(de: Unidade, para: Unidade) {
    super(
      `Não é possível converter ${ROTULO_UNIDADE[de]} em ${ROTULO_UNIDADE[para]}: ` +
        'são grandezas diferentes.',
    );
    this.name = 'UnidadeIncompativelError';
  }
}

/**
 * Converte uma quantidade entre unidades da mesma família.
 * Lança se as unidades não forem compatíveis.
 */
export function converter(quantidade: number, de: Unidade, para: Unidade): number {
  if (de === para) return quantidade;
  if (!mesmaFamilia(de, para)) throw new UnidadeIncompativelError(de, para);
  return (quantidade * PARA_BASE[de]) / PARA_BASE[para];
}
