export interface FipeItem {
  codigo: string;
  nome: string;
}

export interface DetalheMarca {
  modelos: FipeItem[];
  anos: FipeItem[];
}

export interface Marca extends FipeItem {}
export interface Anos extends FipeItem {}
export interface Veiculo {
  Valor: string;
  Marca: string;
  Modelo: string;
  AnoModelo: number;
  Combustivel: string;
  CodigoFipe: string;
  MesReferencia: string;
}