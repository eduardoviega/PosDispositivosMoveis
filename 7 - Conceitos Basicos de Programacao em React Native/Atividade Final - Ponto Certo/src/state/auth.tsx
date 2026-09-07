/**
 * Sessão do usuário — leve, sem dados do Firestore. Fica no topo da árvore,
 * acima de `login.tsx` e do grupo `(protected)`, para os dois saberem o
 * estado de login sem duplicar a assinatura do Firebase Auth.
 *
 * `DadosProvider` (em `state/dados.tsx`) é outra coisa: carrega perfil,
 * materiais e projetos, e só existe dentro do grupo `(protected)`, depois que
 * a sessão já está confirmada.
 */

import { createContext, ReactNode, useContext, useEffect, useState } from 'react';

import { observarUsuario, Usuario } from '@/service/auth/cliente';

type SessaoContexto = {
  /** undefined = ainda não sabemos; null = deslogado; Usuario = sessão ativa. */
  usuario: Usuario | null | undefined;
};

const SessaoContext = createContext<SessaoContexto | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null | undefined>(undefined);

  useEffect(() => observarUsuario(setUsuario), []);

  return <SessaoContext.Provider value={{ usuario }}>{children}</SessaoContext.Provider>;
}

export function useSessao(): SessaoContexto {
  const ctx = useContext(SessaoContext);
  if (!ctx) throw new Error('useSessao precisa estar dentro de AuthProvider.');
  return ctx;
}
