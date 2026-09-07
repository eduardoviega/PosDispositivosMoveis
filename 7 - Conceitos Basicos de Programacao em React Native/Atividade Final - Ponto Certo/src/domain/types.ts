/**
 * Tipos do domínio do Ponto Certo.
 *
 * Todo valor monetário é inteiro em centavos. A única exceção é `custoUnitario`,
 * que guarda centavos com fração de propósito (um novelo de 200 g a R$ 18,90
 * custa 9,45 centavos por grama) e só é arredondado no total da linha.
 */

export type Centavos = number;

// ---------------------------------------------------------------- unidades

export const UNIDADES = ['g', 'kg', 'm', 'cm', 'un'] as const;
export type Unidade = (typeof UNIDADES)[number];

// ---------------------------------------------------------------- material

export const CATEGORIAS_MATERIAL = [
  'fio',
  'enchimento',
  'aviamento',
  'aplicacao',
  'outro',
] as const;
export type CategoriaMaterial = (typeof CATEGORIAS_MATERIAL)[number];

export type Material = {
  id: string;
  nome: string;
  categoria: CategoriaMaterial;
  marca?: string;
  cor?: string;
  unidade: Unidade;
  /** Quantidade que vem na embalagem, na unidade do material. */
  qtdEmbalagem: number;
  precoEmbalagem: Centavos;
  arquivado: boolean;
  criadoEm: string;
};

/** Derivado, nunca digitado. Centavos por unidade, com fração. */
export function custoUnitario(material: Material): number {
  if (material.qtdEmbalagem <= 0) return 0;
  return material.precoEmbalagem / material.qtdEmbalagem;
}

// ---------------------------------------------------------------- projeto

export const CATEGORIAS_PROJETO = [
  'amigurumi',
  'bolsa',
  'tapete',
  'roupa',
  'acessorio',
  'decoracao',
  'outro',
] as const;
export type CategoriaProjeto = (typeof CATEGORIAS_PROJETO)[number];

export const DIFICULDADES = ['facil', 'media', 'dificil'] as const;
export type Dificuldade = (typeof DIFICULDADES)[number];

export const STATUS_PROJETO = ['rascunho', 'precificado', 'vendido'] as const;
export type StatusProjeto = (typeof STATUS_PROJETO)[number];

/**
 * Linha de composição da peça. Guarda o custo congelado no momento do
 * lançamento: mexer no catálogo depois não reescreve o passado.
 */
export type Consumo = {
  id: string;
  materialId: string;
  materialNome: string;
  unidade: Unidade;
  quantidade: number;
  custoUnitarioSnapshot: number;
};

/** Resultado do cálculo, congelado quando a artesã confirma o preço. */
export type Precificacao = {
  custoMaterial: Centavos;
  custoMaoObra: Centavos;
  custoIndiretos: Centavos;
  custoTotal: Centavos;
  precoBase: Centavos;
  precoComTaxa: Centavos;
  precoSugerido: Centavos;
  precoFinal: Centavos;
  taxaValor: Centavos;
  lucroLiquido: Centavos;
  /** Fração: 0,359 = 35,9%. */
  margemReal: number;
  retornoHora: Centavos;
  /** Cópia dos parâmetros usados, para explicar o preço meses depois. */
  perfilSnapshot: PerfilPrecificacao;
  calculadoEm: string;
};

export type Projeto = {
  id: string;
  nome: string;
  descricao?: string;
  horas: number;
  categoria: CategoriaProjeto;
  dificuldade: Dificuldade;
  status: StatusProjeto;
  /** Miniatura ~200 px em base64, para a lista. */
  fotoThumb?: string;
  /** Foto de 1080 px em base64. Em F3 migra para o doc `midia/foto`. */
  fotoCheia?: string;
  consumos: Consumo[];
  precificacao?: Precificacao;
  criadoEm: string;
};

// ---------------------------------------------------------------- perfil

export const PASSOS_ARREDONDAMENTO = [0, 50, 100, 500] as const;
export type PassoArredondamento = (typeof PASSOS_ARREDONDAMENTO)[number];

export type PerfilPrecificacao = {
  valorHora: Centavos;
  /** Frações: 0,10 = 10%. */
  percIndiretos: number;
  percLucro: number;
  percTaxas: number;
  arredondamento: PassoArredondamento;
};

/** Valores com que a conta nasce, para o app já calcular algo. */
export const PERFIL_PADRAO: PerfilPrecificacao = {
  valorHora: 2500,
  percIndiretos: 0.1,
  percLucro: 0.6,
  percTaxas: 0.05,
  arredondamento: 500,
};

// ---------------------------------------------------------------- IA

export type TipoSugestao = 'defesa' | 'anuncio' | 'ideias';

export type DefesaPreco = {
  resposta: string;
  pontos: string[];
};

export type Anuncio = {
  titulo: string;
  descricao: string;
  bullets: string[];
  hashtags: string[];
};

export type Ideia = {
  peca: string;
  categoria: CategoriaProjeto;
  horasEstimadas: number;
  dificuldade: Dificuldade;
  rende: string;
  materiaisExtra: string[];
};

/** Toda saída de IA diz se veio do modelo ou do fallback local. */
export type ResultadoIA<T> = {
  dados: T;
  origem: 'ia' | 'fallback';
  aviso?: string;
};
