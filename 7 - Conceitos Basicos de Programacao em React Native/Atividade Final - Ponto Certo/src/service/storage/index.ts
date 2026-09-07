/**
 * Ponto único de acesso à persistência.
 *
 * O repositório ativo é sempre o do usuário logado no momento — `DadosProvider`
 * chama `definirUsuario` a cada mudança de sessão (login, logout, troca de
 * conta). Nenhuma tela importa `firestore.ts` diretamente.
 */

import { criarRepositorioFirestore } from './firestore';
import type { Repositorio } from './repositorio';

let repositorioAtual: Repositorio | null = null;

export function definirUsuario(uid: string | null): void {
  repositorioAtual = uid ? criarRepositorioFirestore(uid) : null;
}

export function obterRepositorio(): Repositorio {
  if (!repositorioAtual) {
    throw new Error('Nenhum usuário autenticado — não há repositório ativo.');
  }
  return repositorioAtual;
}

export type { PerfilGoogle, Repositorio } from './repositorio';

/** Id curto e ordenável para documentos criados no aparelho. */
export function novoId(): string {
  return `${Date.now().toString(36)}${Math.random().toString(36).slice(2, 7)}`;
}
