import FipeScreen from "@/components/FipeScreen";
import { fetcher } from "@/services/fetcher";
import { useRouter } from "expo-router";
import useSWR from "swr";

export default function Index() {
  const router = useRouter();

  const { data } = useSWR("/carros/marcas", fetcher);

  return <FipeScreen data={data} />;
}
