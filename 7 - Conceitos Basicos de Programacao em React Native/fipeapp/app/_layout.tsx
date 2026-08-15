import { Stack } from "expo-router";

export default function RootLayout() {
  return <Stack>
    <Stack.Screen name="index" options={{ title: "Login" }} />
    <Stack.Screen name="register" options={{ title: "Cadastro" }} />
    <Stack.Screen name="home" options={{ title: "Tela Inicial" }} />
  </Stack>;
}
