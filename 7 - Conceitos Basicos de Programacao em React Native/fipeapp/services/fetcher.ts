import axios from "axios";

axios.defaults.baseURL = "https://parallelum.com.br/fipe/api/v1";

export const fetcher = async (url: string) => {
    const response = await axios.get(url);
    return response.data;
};