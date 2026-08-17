import { Veiculo } from "@/modelos";
import { fetcher } from "@/services/fetcher";
import { useLocalSearchParams } from "expo-router";
import { ActivityIndicator, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import useSWR from "swr";

export default function VeiculoDetalhe() {
  const { codigoMarca, codigoModelo, codigoAno } = useLocalSearchParams();

  const { data, error, isLoading, mutate } = useSWR<Veiculo>(
    `/carros/marcas/${codigoMarca}/modelos/${codigoModelo}/anos/${codigoAno}`,
    fetcher,
    {
      dedupingInterval: 60_000, // 60 segundos
    },
  );

  if (isLoading && !data) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#333" />
        <Text style={styles.statusText}>Carregando detalhes do veiculo...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.centered}>
        <Text style={styles.errorText}>Nao foi possivel carregar os dados.</Text>
        <Text style={styles.errorMessage}>{error.message}</Text>
      </View>
    );
  }

  if (!data) {
    return (
      <View style={styles.centered}>
        <Text style={styles.errorText}>Nenhum dado de veiculo foi encontrado.</Text>
      </View>
    );
  }

  const detalhes = [
    { label: "Valor", value: data.Valor },
    { label: "Marca", value: data.Marca },
    { label: "Modelo", value: data.Modelo },
    { label: "Ano modelo", value: String(data.AnoModelo) },
    { label: "Combustivel", value: data.Combustivel },
    { label: "Codigo FIPE", value: data.CodigoFipe },
    { label: "Mes de referencia", value: data.MesReferencia },
  ];

  return (
    <ScrollView
      contentContainerStyle={styles.container}
      refreshControl={<RefreshControl refreshing={isLoading} onRefresh={() => void mutate()} />}
    >
      <View style={styles.header}>
        <Text style={styles.title}>{data.Modelo}</Text>
        <Text style={styles.subtitle}>{data.Marca}</Text>
      </View>

      <View style={styles.card}>
        {detalhes.map((item) => (
          <View key={item.label} style={styles.row}>
            <Text style={styles.label}>{item.label}</Text>
            <Text style={styles.value}>{item.value}</Text>
          </View>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    padding: 16,
    backgroundColor: "#f5f5f5",
  },
  centered: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
  },
  statusText: {
    marginTop: 12,
    color: "#333",
  },
  errorText: {
    color: "#b00020",
    fontSize: 16,
    fontWeight: "600",
  },
  errorMessage: {
    marginTop: 8,
    color: "#555",
    textAlign: "center",
  },
  header: {
    marginBottom: 16,
    padding: 16,
    borderRadius: 12,
    backgroundColor: "#1f2937",
  },
  title: {
    color: "#fff",
    fontSize: 24,
    fontWeight: "700",
  },
  subtitle: {
    color: "#d1d5db",
    marginTop: 4,
    fontSize: 14,
  },
  card: {
    borderRadius: 12,
    backgroundColor: "#fff",
    overflow: "hidden",
  },
  row: {
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: "#d4d4d8",
  },
  label: {
    color: "#6b7280",
    fontSize: 12,
    textTransform: "uppercase",
  },
  value: {
    marginTop: 4,
    color: "#111827",
    fontSize: 16,
    fontWeight: "600",
  },
});