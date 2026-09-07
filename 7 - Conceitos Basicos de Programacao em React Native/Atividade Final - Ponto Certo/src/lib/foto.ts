/**
 * Foto do projeto em base64.
 *
 * Gera duas versões: miniatura de 200 px para a lista e foto de 1080 px para a
 * tela de detalhe. O limite de 1 MiB por documento do Firestore é a restrição
 * real — base64 infla 33%, então o JPEG precisa ficar abaixo de ~750 KB, e
 * recomprimimos quando passa.
 *
 * A miniatura vai inteira no documento do projeto; a foto cheia mora à parte,
 * em `projetos/{id}/midia/foto` — ver `service/storage/firestore.ts`.
 */

import { ImageManipulator, SaveFormat } from 'expo-image-manipulator';
import * as ImagePicker from 'expo-image-picker';

export const LIMITE_BASE64_BYTES = 700 * 1024;

export type FotoProcessada = { thumb: string; cheia: string };

async function comprimir(
  uri: string,
  largura: number,
  qualidade: number,
): Promise<string> {
  const contexto = ImageManipulator.manipulate(uri);
  contexto.resize({ width: largura });
  const renderizada = await contexto.renderAsync();
  const resultado = await renderizada.saveAsync({
    format: SaveFormat.JPEG,
    compress: qualidade,
    base64: true,
  });
  if (!resultado.base64) throw new Error('Não foi possível processar a imagem.');
  return resultado.base64;
}

/** Reduz até caber no limite; cada passo perde qualidade, não dimensão. */
async function comprimirAteCaber(uri: string): Promise<string> {
  const tentativas: [number, number][] = [
    [1080, 0.6],
    [1080, 0.4],
    [800, 0.4],
    [640, 0.3],
  ];

  let ultima = '';
  for (const [largura, qualidade] of tentativas) {
    ultima = await comprimir(uri, largura, qualidade);
    if (ultima.length <= LIMITE_BASE64_BYTES) return ultima;
  }
  return ultima;
}

async function processar(uri: string): Promise<FotoProcessada> {
  const [thumb, cheia] = await Promise.all([
    comprimir(uri, 200, 0.5),
    comprimirAteCaber(uri),
  ]);
  return { thumb, cheia };
}

export function paraFonteImagem(base64: string): { uri: string } {
  return { uri: `data:image/jpeg;base64,${base64}` };
}

/** Devolve null quando a artesã cancela ou nega a permissão. */
export async function escolherDaGaleria(): Promise<FotoProcessada | null> {
  const permissao = await ImagePicker.requestMediaLibraryPermissionsAsync();
  if (!permissao.granted) return null;

  const r = await ImagePicker.launchImageLibraryAsync({
    mediaTypes: ['images'],
    allowsEditing: true,
    aspect: [1, 1],
    quality: 1,
  });
  if (r.canceled || !r.assets?.length) return null;

  return processar(r.assets[0].uri);
}

export async function tirarFoto(): Promise<FotoProcessada | null> {
  const permissao = await ImagePicker.requestCameraPermissionsAsync();
  if (!permissao.granted) return null;

  const r = await ImagePicker.launchCameraAsync({
    allowsEditing: true,
    aspect: [1, 1],
    quality: 1,
  });
  if (r.canceled || !r.assets?.length) return null;

  return processar(r.assets[0].uri);
}
