/**
 * Contrato de persistência.
 *
 * Existe para que a F3 troque o Firestore por baixo sem tocar em nenhuma tela:
 * o app inteiro fala com esta interface, e só `storage/index.ts` sabe qual
 * implementação está ativa.
 */

import { Material, PerfilPrecificacao, Projeto } from '@/domain/types';

export type PerfilGoogle = {
  nome: string | null;
  email: string | null;
  fotoUrl: string | null;
};

export interface Repositorio {
  /**
   * No primeiro login, cria o documento do usuário com os dados do
   * Google e os valores padrão de precificação. Em logins seguintes, só
   * atualiza nome/e-mail/foto — nunca sobrescreve percentuais já ajustados.
   */
  garantirUsuario(perfilGoogle: PerfilGoogle): Promise<void>;

  carregarPerfil(): Promise<PerfilPrecificacao>;
  salvarPerfil(perfil: PerfilPrecificacao): Promise<void>;

  listarMateriais(): Promise<Material[]>;
  salvarMaterial(material: Material): Promise<void>;
  /** Material referenciado é arquivado, nunca excluído. */
  arquivarMaterial(id: string): Promise<void>;

  /** Lista sem a foto cheia para não puxar centenas de KB por item. */
  listarProjetos(): Promise<Projeto[]>;
  salvarProjeto(projeto: Projeto): Promise<void>;
  /** Busca a foto cheia sob demanda, só quando a tela de detalhe precisa dela. */
  carregarFotoCheia(projetoId: string): Promise<string | undefined>;
  excluirProjeto(id: string): Promise<void>;

  /** Apaga tudo do usuário — usado na exclusão de conta. */
  limparTudo(): Promise<void>;
}
