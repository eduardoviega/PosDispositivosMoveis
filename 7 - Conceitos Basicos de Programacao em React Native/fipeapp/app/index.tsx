import { Button } from "@react-navigation/elements";
import { useRouter } from "expo-router";
import { Text, View } from "react-native";

export default function Index() {

  const router = useRouter();

  return (
    <View
      style={{
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "yellow",
      }}
    >
      <Text>LOGIN</Text>

      <View style={{ flexDirection: "row", gap: 16 }}>
        <Button onPressIn={() => router.navigate("/register")}>
          Cadastre-se
        </Button>
        
        <Button onPressIn={() => router.replace("/home")}>
          Entrar
        </Button>
      </View>
    </View>
  );
}
