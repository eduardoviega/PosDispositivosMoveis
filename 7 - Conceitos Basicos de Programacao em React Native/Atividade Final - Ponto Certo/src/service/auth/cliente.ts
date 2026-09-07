/**
 * Login e sessão.
 *
 * O único jeito de entrar é a conta Google: sem cadastro, sem senha, sem
 * recuperação. O idToken do Google vira credencial do Firebase Auth, que é
 * quem controla a sessão de fato.
 */

import {
  type AuthCredential,
  type User,
  deleteUser,
  getAuth,
  GoogleAuthProvider,
  onAuthStateChanged,
  reauthenticateWithCredential,
  signOut as sairDoFirebase,
  signInWithCredential,
} from '@react-native-firebase/auth';
import {
  GoogleSignin,
  isSuccessResponse,
} from '@react-native-google-signin/google-signin';

GoogleSignin.configure({
  webClientId: process.env.EXPO_PUBLIC_GOOGLE_WEB_CLIENT_ID,
});

export type Usuario = {
  uid: string;
  nome: string | null;
  email: string | null;
  fotoUrl: string | null;
};

function paraUsuario(firebaseUser: User): Usuario {
  return {
    uid: firebaseUser.uid,
    nome: firebaseUser.displayName,
    email: firebaseUser.email,
    fotoUrl: firebaseUser.photoURL,
  };
}

/** Se a conta Google revogar o acesso, o próximo evento devolve null. */
export function observarUsuario(aoMudar: (usuario: Usuario | null) => void): () => void {
  return onAuthStateChanged(getAuth(), (u) => aoMudar(u ? paraUsuario(u) : null));
}

/** Devolve null quando a artesã cancela o seletor de conta. */
async function obterCredencialGoogle(): Promise<AuthCredential | null> {
  await GoogleSignin.hasPlayServices({ showPlayServicesUpdateDialog: true });
  const resposta = await GoogleSignin.signIn();
  if (!isSuccessResponse(resposta)) return null;

  const { idToken } = resposta.data;
  if (!idToken) throw new Error('O Google não devolveu um token de identidade.');
  return GoogleAuthProvider.credential(idToken);
}

/** Devolve false quando a artesã cancela — não é um erro a mostrar na tela. */
export async function entrarComGoogle(): Promise<boolean> {
  const credencial = await obterCredencialGoogle();
  if (!credencial) return false;
  await signInWithCredential(getAuth(), credencial);
  return true;
}

export async function sair(): Promise<void> {
  await sairDoFirebase(getAuth());
  try {
    await GoogleSignin.signOut();
  } catch {
    // Sem sessão de Google para encerrar — não é erro do usuário.
  }
}

/**
 * Excluir a conta exige reautenticação recente. Pede o Google de novo
 * antes de apagar.
 *
 * `apagarDados` roda depois da reautenticação e antes de apagar o usuário —
 * é a única janela em que a sessão ainda é válida para as regras de
 * segurança do Firestore aceitarem a limpeza. Quem chama passa
 * `repositorio.limparTudo`; este módulo não conhece o Firestore.
 */
export async function excluirConta(apagarDados: () => Promise<void>): Promise<void> {
  const usuario = getAuth().currentUser;
  if (!usuario) throw new Error('Nenhuma sessão ativa.');

  const credencial = await obterCredencialGoogle();
  if (!credencial) throw new Error('Reautenticação cancelada.');

  await reauthenticateWithCredential(usuario, credencial);
  await apagarDados();
  await deleteUser(usuario);
  try {
    await GoogleSignin.signOut();
  } catch {
    // Sem sessão de Google para encerrar — não é erro do usuário.
  }
}
