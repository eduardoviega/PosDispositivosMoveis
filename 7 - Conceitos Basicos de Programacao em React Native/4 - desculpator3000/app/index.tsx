import { geradorDesculpa } from "@/service/ai/generator";
import styles from "@/styles";
import { MotiView } from "moti";
import { useState } from "react";
import { Text, TextInput, TouchableOpacity, View } from "react-native";



export default function Index() {
  const [evento, setEvento] = useState("");
  const [resposta, setResposta] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const callDesculpa = async () => {
    setResposta("");
    setIsLoading(true)
    const desculpa = await geradorDesculpa(evento);
    setResposta(desculpa);

    setIsLoading(false)
    setEvento("");

  };

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>Desculpator 3000</Text>
      <Text style={styles.subtitulo}>Sua máquina de desculpas profissional</Text>

      <TextInput
        value={evento}
        placeholder="Digite o evento que você quer evitar ..."
        onChangeText={setEvento}
        style={styles.input}
      />

      <TouchableOpacity style={styles.button} onPress={callDesculpa}>
        <Text style={styles.buttonText}>{
          isLoading ? "Carregando..." : "Gerar Desculpa infalível"
        }</Text>
      </TouchableOpacity>

      {resposta && (
        <MotiView
          style={styles.card}
          from={{ opacity: 0, translateY: 100 }}
          animate={{ opacity: 1, translateY: 0 }}
          transition={{ type: "spring", stiffness: 100 }}
        >
          <Text style={styles.cardTitle}>Sua desculpa está pronta </Text>
          <Text style={styles.cardText}>{resposta}</Text>
        </MotiView>
      )}
    </View>
  );
}
