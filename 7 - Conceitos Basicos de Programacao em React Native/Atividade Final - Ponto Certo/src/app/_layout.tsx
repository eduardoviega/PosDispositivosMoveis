import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { ActivityIndicator, useColorScheme, View } from 'react-native';

import { AuthProvider, useSessao } from '@/state/auth';
import { paletas } from '@/theme';

function Navegacao() {
  const { usuario } = useSessao();
  const escuro = useColorScheme() === 'dark';
  const cores = escuro ? paletas.escuro : paletas.claro;

  if (usuario === undefined) {
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: cores.fundo,
          alignItems: 'center',
          justifyContent: 'center',
        }}>
        <ActivityIndicator color={cores.acento} />
      </View>
    );
  }

  // As duas rotas ficam sempre declaradas — quem decide quem pode ver o quê é
  // o <Redirect> dentro de cada uma (login.tsx e (protected)/_layout.tsx),
  // nunca a ausência de um <Stack.Screen>. Ver a nota em (protected)/_layout.tsx.
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: cores.superficie },
        headerTintColor: cores.acento,
        headerTitleStyle: { color: cores.tinta, fontWeight: '700' },
        contentStyle: { backgroundColor: cores.fundo },
      }}>
      <Stack.Screen name="login" options={{ headerShown: false }} />
      <Stack.Screen name="(protected)" options={{ headerShown: false }} />
    </Stack>
  );
}

export default function RootLayout() {
  const escuro = useColorScheme() === 'dark';

  return (
    <>
      <StatusBar style={escuro ? 'light' : 'dark'} />
      <AuthProvider>
        <Navegacao />
      </AuthProvider>
    </>
  );
}
