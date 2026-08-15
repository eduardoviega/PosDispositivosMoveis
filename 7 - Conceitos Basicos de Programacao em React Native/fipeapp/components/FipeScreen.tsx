import { styles } from "@/components/styles";
import { Ionicons } from "@expo/vector-icons";
import { FlashList } from "@shopify/flash-list";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Text, TextInput, View } from "react-native";

interface IFipeScreen {
  data?: [];
}

export default function FipeScreen({ data }: IFipeScreen) {
  const router = useRouter();
  const [search, setSearch] = useState("");

  const filteredData = data?.filter((item) =>
    item?.nome?.toLowerCase().includes(search.toLowerCase()),
  );

  const renderItem = ({ item }: any) => (
    <View style={styles.item}>
      <Text>{item.nome}</Text>
      <Ionicons name="chevron-forward" size={24} color="black" />
    </View>
  );

  return (
    <View style={{ flex: 1 }}>
      <TextInput
        value={search}
        onChangeText={setSearch}
        style={styles.textInput}
        placeholder="Buscar ..."
      />
      <FlashList
        style={{ flex: 1 }}
        data={filteredData}
        renderItem={renderItem}
      />
    </View>
  );
}
