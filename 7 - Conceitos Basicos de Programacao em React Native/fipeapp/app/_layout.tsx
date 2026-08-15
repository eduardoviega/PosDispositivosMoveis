import { Stack } from "expo-router";

export default function RootLayout() {
  return (
    <Stack>
      <Stack.Screen name="index" options={{ title: "Fipe App" }} />
      <Stack.Screen name="modelos" options={{ title: "Modelos" }} />
      <Stack.Screen name="anos" options={{ title: "Anos" }} />
      <Stack.Screen name="veiculo" options={{ title: "Veículo" }} />
    </Stack>
  );
}