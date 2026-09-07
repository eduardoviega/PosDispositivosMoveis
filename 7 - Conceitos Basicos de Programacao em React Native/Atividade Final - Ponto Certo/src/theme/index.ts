/**
 * Identidade visual do Ponto Certo: neutros frios com um único acento ameixa,
 * a mesma paleta do documento de escopo. A cor forte aparece em um lugar por
 * tela; o resto fica quieto.
 */

import { useColorScheme } from 'react-native';

const claro = {
  fundo: '#F5F4F8',
  superficie: '#FFFFFF',
  superficieAlt: '#EFEDF4',
  tinta: '#1D1922',
  tinta2: '#595265',
  tinta3: '#857D93',
  linha: '#E1DEE8',
  acento: '#7A2E52',
  acento2: '#A8517A',
  acentoLavado: '#F3E8EE',
  positivo: '#35624F',
  positivoLavado: '#E6EFEA',
  atencao: '#7E5410',
  atencaoLavado: '#F6EEDE',
  erro: '#96253A',
  erroLavado: '#F8E7EA',
  // Fatias da barra de composição do preço, na ordem da cascata.
  fatias: ['#7A2E52', '#A8517A', '#C99DB2', '#35624F', '#9A93A6'],
};

const escuro: typeof claro = {
  fundo: '#151219',
  superficie: '#1E1A25',
  superficieAlt: '#262030',
  tinta: '#EDE9F3',
  tinta2: '#ABA2B8',
  tinta3: '#857C93',
  linha: '#302A3B',
  acento: '#E39BB6',
  acento2: '#C97F9D',
  acentoLavado: '#2C1F29',
  positivo: '#85C4A6',
  positivoLavado: '#1B2B24',
  atencao: '#D7A752',
  atencaoLavado: '#2B2317',
  erro: '#E79AA6',
  erroLavado: '#2E1B20',
  fatias: ['#E39BB6', '#C97F9D', '#966880', '#85C4A6', '#6E6679'],
};

export type Cores = typeof claro;

export const esp = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
  xxl: 32,
} as const;

export const raio = {
  sm: 6,
  md: 10,
  lg: 14,
  pilula: 999,
} as const;

export const tipo = {
  titulo: { fontSize: 26, fontWeight: '700' as const, letterSpacing: -0.5 },
  secao: { fontSize: 18, fontWeight: '700' as const },
  corpo: { fontSize: 15, fontWeight: '400' as const },
  rotulo: { fontSize: 11, fontWeight: '700' as const, letterSpacing: 1.1 },
  pequeno: { fontSize: 13, fontWeight: '400' as const },
  numero: { fontSize: 15, fontWeight: '600' as const },
  destaque: { fontSize: 34, fontWeight: '700' as const, letterSpacing: -1 },
} as const;

export function useCores(): Cores {
  return useColorScheme() === 'dark' ? escuro : claro;
}

export const paletas = { claro, escuro };
