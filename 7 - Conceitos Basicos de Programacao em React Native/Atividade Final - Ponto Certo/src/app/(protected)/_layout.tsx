import { Redirect, Stack } from 'expo-router';

import { useSessao } from '@/state/auth';
import { DadosProvider } from '@/state/dados';
import { useCores } from '@/theme';

/**
 * Portão de autenticação do grupo.
 *
 * Importante: o Expo Router descobre rotas pelo sistema de arquivos, não
 * pelos `<Stack.Screen>` presentes num render específico. Omitir a
 * declaração de uma tela não impede o router de tentar renderizá-la se a
 * navegação apontar para ela — isso já causou "useDados fora do
 * DadosProvider" quando a raiz alternava quais `<Stack.Screen>` existiam
 * conforme o login. A forma correta é: toda rota sempre declarada, e quem
 * barra o acesso é o `<Redirect>` aqui dentro.
 */
export default function ProtectedLayout() {
  const { usuario } = useSessao();
  const c = useCores();

  if (!usuario) return <Redirect href="/login" />;

  return (
    <DadosProvider usuario={usuario}>
      <Stack
        screenOptions={{
          headerStyle: { backgroundColor: c.superficie },
          headerTintColor: c.acento,
          headerTitleStyle: { color: c.tinta, fontWeight: '700' },
          contentStyle: { backgroundColor: c.fundo },
        }}>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="material/[id]" options={{ title: 'Material' }} />
        <Stack.Screen name="projeto/[id]" options={{ headerShown: false }} />
      </Stack>
    </DadosProvider>
  );
}
