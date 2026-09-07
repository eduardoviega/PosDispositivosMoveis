/**
 * O texto devolvido pela IA não pode citar valor que não foi enviado no
 * prompt. Extraímos os valores monetários da resposta e conferimos contra o
 * conjunto permitido; número novo invalida a resposta e vale o fallback.
 */

const VALOR_MONETARIO = /R\$\s?\d{1,3}(?:\.\d{3})*(?:,\d{2})?|\d+,\d{2}/g;

function normalizar(valor: string): string {
  return valor
    .replace(/R\$/gi, '')
    .replace(/\s/g, '')
    .replace(/\./g, '');
}

export function valoresCitados(texto: string): string[] {
  return (texto.match(VALOR_MONETARIO) ?? []).map(normalizar);
}

/**
 * `permitidos` são os valores que o app colocou no prompt, já formatados em
 * reais (ex.: "325,00").
 */
export function citaValorInventado(texto: string, permitidos: string[]): boolean {
  const conjunto = new Set(permitidos.map(normalizar));
  return valoresCitados(texto).some((v) => !conjunto.has(v));
}

export class RespostaInvalidaError extends Error {
  constructor(motivo: string) {
    super(`Resposta da IA descartada: ${motivo}`);
    this.name = 'RespostaInvalidaError';
  }
}

/** Resposta fora do formato é tratada como falha. */
export function exigirTexto(valor: unknown, campo: string): string {
  if (typeof valor !== 'string' || valor.trim() === '') {
    throw new RespostaInvalidaError(`campo "${campo}" ausente ou vazio`);
  }
  return valor.trim();
}

export function exigirListaDeTexto(valor: unknown, campo: string): string[] {
  if (!Array.isArray(valor) || valor.some((v) => typeof v !== 'string')) {
    throw new RespostaInvalidaError(`campo "${campo}" não é lista de texto`);
  }
  return valor as string[];
}
