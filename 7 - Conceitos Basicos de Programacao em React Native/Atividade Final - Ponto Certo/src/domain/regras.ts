/**
 * Regras que dependem de mais de uma entidade e não caberiam no módulo puro de
 * cálculo.
 */

import { custoUnitario, Material, Projeto } from './types';

/**
 * Mexer no preço do material no catálogo não altera a precificação
 * gravada. O projeto passa a exibir "custo desatualizado" e pode ser
 * recalculado — e o recálculo sobrescreve, sem guardar versão.
 */
export function custoDesatualizado(projeto: Projeto, materiais: Material[]): boolean {
  if (!projeto.precificacao) return false;

  return projeto.consumos.some((consumo) => {
    const material = materiais.find((m) => m.id === consumo.materialId);
    if (!material) return false;
    const atual = custoUnitario(material);
    // Tolerância de meio centavo por unidade: diferença abaixo disso não muda
    // nenhum total exibido.
    return Math.abs(atual - consumo.custoUnitarioSnapshot) > 0.005;
  });
}

export const ROTULO_STATUS = {
  rascunho: 'rascunho',
  precificado: 'precificado',
  vendido: 'vendido',
} as const;

export const ROTULO_CATEGORIA_PROJETO = {
  amigurumi: 'Amigurumi',
  bolsa: 'Bolsa',
  tapete: 'Tapete',
  roupa: 'Roupa',
  acessorio: 'Acessório',
  decoracao: 'Decoração',
  outro: 'Outro',
} as const;

export const ROTULO_CATEGORIA_MATERIAL = {
  fio: 'Fio',
  enchimento: 'Enchimento',
  aviamento: 'Aviamento',
  aplicacao: 'Aplicação',
  outro: 'Outro',
} as const;

export const ROTULO_DIFICULDADE = {
  facil: 'Fácil',
  media: 'Média',
  dificil: 'Difícil',
} as const;
