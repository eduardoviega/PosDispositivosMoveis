/**
 * Contexto único de dados: perfil de precificação, catálogo de materiais e
 * projetos. Fala só com `@/service/storage`, nunca com o Firestore
 * diretamente — nenhuma tela muda se a persistência trocar de novo.
 */

import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';

import { Material, PERFIL_PADRAO, PerfilPrecificacao, Projeto } from '@/domain/types';
import type { Usuario } from '@/service/auth/cliente';
import { definirUsuario, obterRepositorio } from '@/service/storage';

type Contexto = {
  usuario: Usuario;
  carregando: boolean;
  perfil: PerfilPrecificacao;
  materiais: Material[];
  materiaisAtivos: Material[];
  projetos: Projeto[];
  salvarPerfil: (perfil: PerfilPrecificacao) => Promise<void>;
  salvarMaterial: (material: Material) => Promise<void>;
  arquivarMaterial: (id: string) => Promise<void>;
  salvarProjeto: (projeto: Projeto) => Promise<void>;
  excluirProjeto: (id: string) => Promise<void>;
  /** Busca a foto cheia sob demanda — ela não vem na lista. */
  carregarFotoCheia: (id: string) => Promise<string | undefined>;
  /** Apaga materiais, projetos e o perfil do usuário atual — usado na exclusão de conta. */
  apagarTudo: () => Promise<void>;
  materialPorId: (id: string) => Material | undefined;
  projetoPorId: (id: string) => Projeto | undefined;
  /** Material usado em algum projeto não pode ser excluído. */
  materialEmUso: (id: string) => boolean;
};

const DadosContext = createContext<Contexto | null>(null);

export function DadosProvider({
  children,
  usuario,
}: {
  children: ReactNode;
  usuario: Usuario;
}) {
  const [carregando, setCarregando] = useState(true);
  const [perfil, setPerfil] = useState<PerfilPrecificacao>(PERFIL_PADRAO);
  const [materiais, setMateriais] = useState<Material[]>([]);
  const [projetos, setProjetos] = useState<Projeto[]>([]);

  useEffect(() => {
    definirUsuario(usuario.uid);

    let ativo = true;
    setCarregando(true);
    (async () => {
      const repo = obterRepositorio();
      // Cria o documento do usuário no primeiro login; em logins
      // seguintes, só atualiza nome/e-mail/foto.
      await repo.garantirUsuario({
        nome: usuario.nome,
        email: usuario.email,
        fotoUrl: usuario.fotoUrl,
      });
      const [p, m, pr] = await Promise.all([
        repo.carregarPerfil(),
        repo.listarMateriais(),
        repo.listarProjetos(),
      ]);
      if (!ativo) return;
      setPerfil(p);
      setMateriais(m);
      setProjetos(pr);
      setCarregando(false);
    })();
    return () => {
      ativo = false;
    };
  }, [usuario]);

  const salvarPerfil = useCallback(async (novo: PerfilPrecificacao) => {
    setPerfil(novo);
    await obterRepositorio().salvarPerfil(novo);
  }, []);

  const salvarMaterial = useCallback(async (material: Material) => {
    setMateriais((atual) => {
      const i = atual.findIndex((m) => m.id === material.id);
      if (i === -1) return [material, ...atual];
      const copia = [...atual];
      copia[i] = material;
      return copia;
    });
    await obterRepositorio().salvarMaterial(material);
  }, []);

  const arquivarMaterial = useCallback(async (id: string) => {
    setMateriais((atual) => atual.map((m) => (m.id === id ? { ...m, arquivado: true } : m)));
    await obterRepositorio().arquivarMaterial(id);
  }, []);

  const salvarProjeto = useCallback(async (projeto: Projeto) => {
    setProjetos((atual) => {
      const i = atual.findIndex((p) => p.id === projeto.id);
      if (i === -1) return [projeto, ...atual];
      const copia = [...atual];
      copia[i] = projeto;
      return copia;
    });
    await obterRepositorio().salvarProjeto(projeto);
  }, []);

  const excluirProjeto = useCallback(async (id: string) => {
    setProjetos((atual) => atual.filter((p) => p.id !== id));
    await obterRepositorio().excluirProjeto(id);
  }, []);

  const carregarFotoCheia = useCallback(
    (id: string) => obterRepositorio().carregarFotoCheia(id),
    [],
  );

  const apagarTudo = useCallback(() => obterRepositorio().limparTudo(), []);

  const valor = useMemo<Contexto>(
    () => ({
      usuario,
      carregando,
      perfil,
      materiais,
      materiaisAtivos: materiais.filter((m) => !m.arquivado),
      projetos,
      salvarPerfil,
      salvarMaterial,
      arquivarMaterial,
      salvarProjeto,
      excluirProjeto,
      carregarFotoCheia,
      apagarTudo,
      materialPorId: (id) => materiais.find((m) => m.id === id),
      projetoPorId: (id) => projetos.find((p) => p.id === id),
      materialEmUso: (id) =>
        projetos.some((p) => p.consumos.some((c) => c.materialId === id)),
    }),
    [
      usuario,
      carregando,
      perfil,
      materiais,
      projetos,
      salvarPerfil,
      salvarMaterial,
      arquivarMaterial,
      salvarProjeto,
      excluirProjeto,
      carregarFotoCheia,
      apagarTudo,
    ],
  );

  return <DadosContext.Provider value={valor}>{children}</DadosContext.Provider>;
}

export function useDados(): Contexto {
  const ctx = useContext(DadosContext);
  if (!ctx) throw new Error('useDados precisa estar dentro de DadosProvider.');
  return ctx;
}
