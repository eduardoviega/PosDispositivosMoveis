import { SuperButton } from "@/components/SuperButton";
import { SuperTitle } from "@/components/SuperTitle";
import { useState } from "react";
import { Image, StyleSheet, Text, View } from "react-native";

export default function Index() {
  const [count, setCount] = useState(0);

  const addMore = () => {
    setCount(count + 1);
  };

  const logo = require("@/assets/images/react-logo.png");
  const logoUri = "https://reactnative.dev/img/tiny_logo.png";

  return (
    <View style={styles.container}>
      {count > 10 && <SuperTitle title="Cliente Ouro"></SuperTitle>}
      <SuperTitle title={"Contador: " + count}></SuperTitle>
      <SuperTitle title={"Total dos produtos: " + count * 20}></SuperTitle>
      
      <Image source={logo} style={{ width: 100, height: 100 }}></Image>
      <Image source={{ uri: logoUri }} style={{ width: 50, height: 50 }}></Image>

      <Text style={styles.title}>
        {count < 10 ? "Falta item" : "Continuar"}
      </Text>
      <SuperButton title="Mais 1" onPress={addMore}></SuperButton>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "gray",
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: 30,
    fontWeight: "bold",
    color: "yellow",
    marginTop: 20,
  },
});