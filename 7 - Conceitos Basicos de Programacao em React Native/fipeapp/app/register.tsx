import { Button } from "@react-navigation/elements";
import { useRouter } from "expo-router";
import { Text, View } from "react-native";

export default function Register() {
  const router = useRouter();

  return (
    <View
      style={{
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "orange",
      }}
    >
      <Text>REGISTER</Text>

      <Button onPressIn={() => router.back()}>Voltar</Button>
    </View>
  );
}
