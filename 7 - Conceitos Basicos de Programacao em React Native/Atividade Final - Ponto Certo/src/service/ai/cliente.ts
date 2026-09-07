/**
 * Único ponto de contato com o modelo:* SDK `openai` apontando para o
 * endpoint compatível do Gemini.
 *
 * Nada além disto arquivo muda quando a chave falta ou a rede cai: cada caso
 * de uso em `service/ai/*` já valida o JSON e já tem fallback no `catch`.
 */

import OpenAI from 'openai';

export const MODELO = 'gemini-3.1-flash-lite';

export const TIMEOUT_MS = 12_000;

const gemini = new OpenAI({
  apiKey: process.env.EXPO_PUBLIC_GEMINI_API_KEY,
  baseURL: 'https://generativelanguage.googleapis.com/v1beta/openai/',
  dangerouslyAllowBrowser: true,
});

export class IANaoConfiguradaError extends Error {
  constructor() {
    super('A IA ainda não foi configurada neste projeto.');
    this.name = 'IANaoConfiguradaError';
  }
}

export function iaDisponivel(): boolean {
  return Boolean(process.env.EXPO_PUBLIC_GEMINI_API_KEY);
}

/** O modelo às vezes envolve o JSON em ```json apesar da instrução do prompt. */
function limparCercas(texto: string): string {
  const semCercas = texto.trim().replace(/^```(?:json)?\s*/i, '').replace(/```\s*$/i, '');
  return semCercas.trim();
}

/**
 * Pede uma resposta em JSON ao modelo. Lança sempre que a IA não puder ser
 * usada — chave ausente, rede fora, timeout ou JSON inválido — para que cada
 * caso de uso caia no próprio fallback local.
 */
export async function pedirJson<T>(sistema: string, usuario: string): Promise<T> {
  if (!iaDisponivel()) throw new IANaoConfiguradaError();

  const resultado = await gemini.chat.completions.create(
    {
      model: MODELO,
      messages: [
        { role: 'system', content: sistema },
        { role: 'user', content: usuario },
      ],
      max_tokens: 700,
    },
    { timeout: TIMEOUT_MS },
  );

  const texto = resultado.choices[0]?.message?.content;
  if (!texto) throw new Error('Resposta vazia da IA.');

  return JSON.parse(limparCercas(texto)) as T;
}
