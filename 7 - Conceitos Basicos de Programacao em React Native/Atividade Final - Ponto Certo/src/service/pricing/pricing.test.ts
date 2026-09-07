/// <reference types="jest" />
/**
 * Teste de aceitação da fórmula: o exemplo do coelho amigurumi da seção 5 do
 * documento de escopo. Se a implementação acertar R$ 325,00 e R$ 44,45, a
 * cascata está correta.
 */

import { PerfilPrecificacao } from '@/domain/types';
import { converter, UnidadeIncompativelError } from '@/domain/unidades';
import { lerCentavos, lerNumero } from '@/lib/dinheiro';
import {
  aplicarTaxas,
  avisos,
  calcularPrecificacao,
  custoLinha,
  custoMaterial,
  margemParaMarkup,
  validarEntrada,
  validarPerfil,
  type ConsumoCalculavel,
} from './index';

const perfil: PerfilPrecificacao = {
  valorHora: 2500, // R$ 25,00
  percIndiretos: 0.1,
  percLucro: 0.6,
  percTaxas: 0.05,
  arredondamento: 500, // R$ 5,00
};

/** Fio 100 g / R$ 18,90 · fibra 400 g / R$ 22,00 · olhos 1 par / R$ 3,50 */
const consumosCoelho: ConsumoCalculavel[] = [
  { quantidade: 100, custoUnitarioSnapshot: 1890 / 100 },
  { quantidade: 40, custoUnitarioSnapshot: 2200 / 400 },
  { quantidade: 1, custoUnitarioSnapshot: 350 / 1 },
];

describe('coelho amigurumi — exemplo fechado do escopo', () => {
  const p = calcularPrecificacao({ horas: 6, consumos: consumosCoelho, perfil });

  it('custo de cada linha de material', () => {
    expect(custoLinha(consumosCoelho[0])).toBe(1890); // R$ 18,90
    expect(custoLinha(consumosCoelho[1])).toBe(220); // R$ 2,20
    expect(custoLinha(consumosCoelho[2])).toBe(350); // R$ 3,50
  });

  it('custo de material somado', () => {
    expect(p.custoMaterial).toBe(2460); // R$ 24,60
  });

  it('mão de obra de 6 h a R$ 25,00', () => {
    expect(p.custoMaoObra).toBe(15000); // R$ 150,00
  });

  it('indiretos de 10% sobre o custo direto', () => {
    expect(p.custoIndiretos).toBe(1746); // R$ 17,46
  });

  it('custo total é o piso de venda', () => {
    expect(p.custoTotal).toBe(19206); // R$ 192,06
  });

  it('preço-base com 60% de lucro', () => {
    expect(p.precoBase).toBe(30730); // R$ 307,30
  });

  it('taxa de 5% repassada por divisão, não por soma', () => {
    expect(p.precoComTaxa).toBe(32347); // R$ 323,47
    // O erro clássico daria 307,30 × 1,05 = 322,67 — menos que o correto.
    expect(p.precoComTaxa).toBeGreaterThan(Math.round(30730 * 1.05));
  });

  it('preço sugerido arredondado para R$ 5,00 acima', () => {
    expect(p.precoSugerido).toBe(32500); // R$ 325,00
  });

  it('indicadores de conferência', () => {
    expect(p.taxaValor).toBe(1625); // R$ 16,25
    expect(p.lucroLiquido).toBe(11669); // R$ 116,69
    expect(p.margemReal).toBeCloseTo(0.359, 3); // 35,9%
    expect(p.retornoHora).toBe(4445); // R$ 44,45
  });
});

describe('arredondamento comercial', () => {
  it('é sempre para cima, nunca para o mais próximo', () => {
    const p = calcularPrecificacao({
      horas: 6,
      consumos: consumosCoelho,
      perfil: { ...perfil, arredondamento: 100 },
    });
    expect(p.precoSugerido).toBe(32400); // 323,47 -> 324,00
  });

  it('passo zero mantém o preço com taxa', () => {
    const p = calcularPrecificacao({
      horas: 6,
      consumos: consumosCoelho,
      perfil: { ...perfil, arredondamento: 0 },
    });
    expect(p.precoSugerido).toBe(32347);
  });
});

describe('projeto sem material', () => {
  const p = calcularPrecificacao({ horas: 2, consumos: [], perfil });

  it('calcula só com mão de obra', () => {
    expect(p.custoMaterial).toBe(0);
    expect(p.custoMaoObra).toBe(5000);
    expect(p.custoIndiretos).toBe(500);
    expect(p.custoTotal).toBe(5500);
  });

  it('avisa que o material não foi lançado', () => {
    const lista = avisos(p, 0);
    expect(lista).toHaveLength(1);
    expect(lista[0].tipo).toBe('atencao');
  });
});

describe('preço final abaixo do custo', () => {
  const p = calcularPrecificacao({
    horas: 6,
    consumos: consumosCoelho,
    perfil,
    precoFinal: 15000, // R$ 150,00, abaixo do custo de R$ 192,06
  });

  it('aceita o valor mas devolve margem negativa', () => {
    expect(p.precoFinal).toBe(15000);
    expect(p.lucroLiquido).toBeLessThan(0);
    expect(p.margemReal).toBeLessThan(0);
  });

  it('alerta com o prejuízo em reais', () => {
    const lista = avisos(p, 3);
    expect(lista).toHaveLength(1);
    expect(lista[0].tipo).toBe('erro');
    expect(lista[0].texto).toContain('42,06');
  });
});

describe('preço final editado pela artesã', () => {
  it('recalcula taxa, margem e retorno sobre o valor escolhido', () => {
    const p = calcularPrecificacao({
      horas: 6,
      consumos: consumosCoelho,
      perfil,
      precoFinal: 40000, // R$ 400,00
    });
    expect(p.taxaValor).toBe(2000);
    expect(p.lucroLiquido).toBe(40000 - 19206 - 2000);
    expect(p.retornoHora).toBe(Math.round((40000 - 2460 - 1746 - 2000) / 6));
  });
});

describe('validações', () => {
  it('recusa taxa de 100% em vez de dividir por zero', () => {
    expect(() => aplicarTaxas(10000, 1)).toThrow();
  });

  it('cobra horas maiores que zero', () => {
    const erros = validarEntrada({ horas: 0, consumos: [], perfil });
    expect(erros).toContain('Informe quantas horas a peça levou.');
  });

  it('cobra quantidade maior que zero em cada material', () => {
    const erros = validarEntrada({
      horas: 1,
      consumos: [{ quantidade: 0, custoUnitarioSnapshot: 10 }],
      perfil,
    });
    expect(erros.some((e) => e.includes('quantidade do material 1'))).toBe(true);
  });

  it('respeita os limites de percentual', () => {
    expect(validarPerfil({ ...perfil, percLucro: 6 })).toHaveLength(1);
    expect(validarPerfil({ ...perfil, percTaxas: 0.8 })).toHaveLength(1);
    expect(validarPerfil({ ...perfil, percIndiretos: 1.5 })).toHaveLength(1);
    expect(validarPerfil({ ...perfil, valorHora: 0 })).toHaveLength(1);
    expect(validarPerfil(perfil)).toHaveLength(0);
  });

  it('converte margem desejada em markup', () => {
    expect(margemParaMarkup(0.5)).toBeCloseTo(1);
    expect(margemParaMarkup(0.2)).toBeCloseTo(0.25);
  });
});

describe('unidades', () => {
  it('converte dentro da mesma família', () => {
    expect(converter(1, 'kg', 'g')).toBe(1000);
    expect(converter(250, 'g', 'kg')).toBe(0.25);
    expect(converter(1.5, 'm', 'cm')).toBe(150);
  });

  it('recusa somar peso com contagem', () => {
    expect(() => converter(1, 'g', 'un')).toThrow(UnidadeIncompativelError);
    expect(() => converter(1, 'm', 'g')).toThrow(UnidadeIncompativelError);
  });
});

describe('entrada numérica em pt-BR', () => {
  it('aceita vírgula como separador decimal', () => {
    expect(lerNumero('2,5')).toBe(2.5);
    expect(lerCentavos('18,90')).toBe(1890);
    expect(lerCentavos('R$ 1.234,56')).toBe(123456);
  });

  it('devolve null para texto inválido', () => {
    expect(lerNumero('')).toBeNull();
    expect(lerNumero('abc')).toBeNull();
  });
});

describe('custo de material com quantidades fracionadas', () => {
  it('não acumula erro de centavo', () => {
    // 3 linhas de 33,333 g de um fio a R$ 0,03 por grama.
    const consumos: ConsumoCalculavel[] = Array.from({ length: 3 }, () => ({
      quantidade: 33.333,
      custoUnitarioSnapshot: 3,
    }));
    expect(custoMaterial(consumos)).toBe(300); // 3 × 100 centavos
  });
});
