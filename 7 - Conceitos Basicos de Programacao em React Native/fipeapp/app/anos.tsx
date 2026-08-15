import FipeScreen from "@/components/FipeScreen";
import { Anos } from "@/modelos";
import { fetcher } from "@/services/fetcher";
import { useLocalSearchParams, useRouter } from "expo-router";
import useSWR from "swr";

export default function AnosDetalhe() {
  const router = useRouter();

  const { codigoMarca, codigoModelo } = useLocalSearchParams();

  const { data, error, isLoading, mutate } = useSWR<Anos[]>(
    `/carros/marcas/${codigoMarca}/modelos/${codigoModelo}/anos`,
    fetcher,
    {
      dedupingInterval: 60_000, // 60 segundos
    },
  );

  const goNext = (codigo: string) => {
    console.log("Codigo: ", codigo);
    router.navigate({
      pathname: "/veiculo",
      params: { codigoMarca: codigoMarca, codigoModelo: codigoModelo, codigoAno: codigo },
    });
  };

  return (
    <FipeScreen
      data={data}
      goNext={goNext}
      error={error}
      isLoading={isLoading}
      update={mutate}
    />
  );
}