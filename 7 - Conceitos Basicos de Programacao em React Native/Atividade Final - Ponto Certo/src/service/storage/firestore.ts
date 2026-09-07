/**
 * Persistência em Firestore.
 *
 * Tudo vive sob `usuarios/{uid}`. Cada instância deste repositório já nasce
 * amarrada a um uid; trocar de conta troca de instância inteira, nunca de
 * caminho dentro da mesma instância — é a regra de segurança que faz o
 * isolamento entre usuários valer também no código, não só no servidor.
 */

import {
  collection,
  deleteDoc,
  doc,
  getDoc,
  getDocs,
  getFirestore,
  setDoc,
} from '@react-native-firebase/firestore';

import { Material, PERFIL_PADRAO, PerfilPrecificacao, Projeto } from '@/domain/types';
import { PerfilGoogle, Repositorio } from './repositorio';

const db = getFirestore();

/**
 * A foto cheia não viaja com o resto do projeto: listar peças não deveria
 * puxar centenas de KB por item. Fica num documento à parte, lido só quando a
 * tela de detalhe abre.
 */
type ProjetoSemFotoCheia = Omit<Projeto, 'fotoCheia'>;

/**
 * Campos opcionais do domínio (marca, cor, descrição...) ficam `undefined`
 * quando vazios — ao contrário do AsyncStorage, o Firestore rejeita
 * `undefined` como valor de campo. `JSON` descarta essas chaves ao
 * serializar, então isso limpa o objeto antes de qualquer `setDoc`.
 */
function semIndefinidos<T>(valor: T): T {
  return JSON.parse(JSON.stringify(valor));
}

export function criarRepositorioFirestore(uid: string): Repositorio {
  const usuarioRef = doc(db, 'usuarios', uid);
  const materiaisRef = collection(db, 'usuarios', uid, 'materiais');
  const projetosRef = collection(db, 'usuarios', uid, 'projetos');

  function fotoCheiaRef(projetoId: string) {
    return doc(db, 'usuarios', uid, 'projetos', projetoId, 'midia', 'foto');
  }

  return {
    async garantirUsuario(perfilGoogle: PerfilGoogle) {
      const snap = await getDoc(usuarioRef);
      if (snap.exists()) {
        // Já existe: atualiza só o perfil do Google, preserva os percentuais.
        await setDoc(usuarioRef, perfilGoogle, { merge: true });
        return;
      }
      await setDoc(usuarioRef, { ...PERFIL_PADRAO, ...perfilGoogle });
    },

    async carregarPerfil() {
      const snap = await getDoc(usuarioRef);
      const dados = snap.data() as Partial<PerfilPrecificacao> | undefined;
      return { ...PERFIL_PADRAO, ...dados };
    },

    async salvarPerfil(perfil) {
      await setDoc(usuarioRef, perfil, { merge: true });
    },

    async listarMateriais() {
      const snap = await getDocs(materiaisRef);
      return snap.docs.map((d) => d.data() as Material);
    },

    async salvarMaterial(material) {
      await setDoc(doc(materiaisRef, material.id), semIndefinidos(material));
    },

    async arquivarMaterial(id) {
      await setDoc(doc(materiaisRef, id), { arquivado: true }, { merge: true });
    },

    async listarProjetos() {
      const snap = await getDocs(projetosRef);
      return snap.docs.map((d) => d.data() as Projeto);
    },

    async salvarProjeto(projeto) {
      const { fotoCheia, ...semFotoCheia } = projeto;
      await setDoc(
        doc(projetosRef, projeto.id),
        semIndefinidos(semFotoCheia as ProjetoSemFotoCheia),
      );
      // fotoCheia ausente == a artesã não trocou a foto nesta edição; não mexe
      // no que já está gravado.
      if (fotoCheia) {
        await setDoc(fotoCheiaRef(projeto.id), { base64: fotoCheia });
      }
    },

    async carregarFotoCheia(projetoId) {
      const snap = await getDoc(fotoCheiaRef(projetoId));
      const dados = snap.data() as { base64?: string } | undefined;
      return dados?.base64;
    },

    async excluirProjeto(id) {
      await deleteDoc(fotoCheiaRef(id)).catch(() => undefined);
      await deleteDoc(doc(projetosRef, id));
    },

    async limparTudo() {
      const [materiaisSnap, projetosSnap] = await Promise.all([
        getDocs(materiaisRef),
        getDocs(projetosRef),
      ]);
      await Promise.all([
        ...materiaisSnap.docs.map((d) => deleteDoc(d.ref)),
        ...projetosSnap.docs.map(async (d) => {
          await deleteDoc(fotoCheiaRef(d.id)).catch(() => undefined);
          await deleteDoc(d.ref);
        }),
      ]);
      await deleteDoc(usuarioRef);
    },
  };
}
