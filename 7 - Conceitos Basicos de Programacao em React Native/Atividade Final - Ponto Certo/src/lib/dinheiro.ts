/**
 * Dinheiro em centavos inteiros e entrada numérica em pt-BR.
 * Formatar é a única coisa que acontece na borda; nada aqui volta para float
 * de reais.
 */

import { Centavos } from '@/domain/types';

/** Arredondamento monetário é half-up na segunda casa. */
export function arredondaCentavos(valor: number): Centavos {
  // Math.round já é half-up para positivos; o epsilon protege de ruído de
  // ponto flutuante em casos como 1234.4999999999998.
  return Math.round(valor + (valor >= 0 ? Number.EPSILON : -Number.EPSILON) * Math.abs(valor));
}

/** O arredondamento comercial do preço final é sempre para cima. */
export function arredondaParaCima(centavos: Centavos, passo: number): Centavos {
  if (passo <= 0) return centavos;
  return Math.ceil(centavos / passo) * passo;
}

export function formatarCentavos(centavos: Centavos): string {
  const sinal = centavos < 0 ? '-' : '';
  const abs = Math.abs(Math.round(centavos));
  const reais = Math.floor(abs / 100);
  const resto = abs % 100;
  return `${sinal}${reais.toLocaleString('pt-BR')},${String(resto).padStart(2, '0')}`;
}

export function formatarReais(centavos: Centavos): string {
  return `R$ ${formatarCentavos(centavos)}`;
}

/** Percentual como fração (0,105) para texto curto ("10,5%"). */
export function formatarPercentual(fracao: number, casas = 1): string {
  const v = (fracao * 100).toFixed(casas).replace('.', ',');
  return `${v.replace(/,0$/, '')}%`;
}

/**
 * Aceita vírgula como separador decimal e normaliza antes de calcular.
 * Devolve null quando o texto não é um número utilizável.
 */
export function lerNumero(texto: string): number | null {
  const limpo = texto
    .trim()
    .replace(/\s/g, '')
    .replace(/R\$/gi, '')
    .replace(/\./g, '')
    .replace(',', '.');
  if (limpo === '') return null;
  const n = Number(limpo);
  return Number.isFinite(n) ? n : null;
}

/** Lê um valor em reais digitado pela artesã e devolve centavos inteiros. */
export function lerCentavos(texto: string): Centavos | null {
  const n = lerNumero(texto);
  if (n === null) return null;
  return arredondaCentavos(n * 100);
}

/** Escreve centavos num campo de texto editável (sem "R$"). */
export function escreverCentavos(centavos: Centavos | undefined): string {
  if (centavos === undefined) return '';
  return formatarCentavos(centavos);
}

export function escreverNumero(valor: number | undefined): string {
  if (valor === undefined || Number.isNaN(valor)) return '';
  return String(valor).replace('.', ',');
}
