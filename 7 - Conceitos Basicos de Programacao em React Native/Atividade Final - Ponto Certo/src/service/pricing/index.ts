/**
 * Núcleo de cálculo do Ponto Certo.
 *
 * Módulo puro: nada de React, nada de rede, nada de I/O. É o único lugar do
 * código com testes obrigatórios, porque é a única parte que não pode estar
 * errada. A cascata segue a seção 4 do documento de escopo, na ordem — trocar
 * os dois últimos passos produz preço errado.
 */

import {
  Centavos,
  Consumo,
  PerfilPrecificacao,
  Precificacao,
} from '@/domain/types';
import { arredondaCentavos, arredondaParaCima } from '@/lib/dinheiro';

export type ConsumoCalculavel = Pick<Consumo, 'quantidade' | 'custoUnitarioSnapshot'>;

export type EntradaCalculo = {
  horas: number;
  consumos: ConsumoCalculavel[];
  perfil: PerfilPrecificacao;
  /** Preço que a artesã decidiu cobrar. Ausente = usa o sugerido. */
  precoFinal?: Centavos;
};

// ------------------------------------------------------------------ limites

export const LIMITES = {
  percLucro: { min: 0, max: 5 },
  percIndiretos: { min: 0, max: 1 },
  percTaxas: { min: 0, max: 0.5 },
} as const;

// ------------------------------------------------------------------ passos

/** Passo 2: custo da linha de consumo, arredondado só aqui. */
export function custoLinha(consumo: ConsumoCalculavel): Centavos {
  return arredondaCentavos(consumo.quantidade * consumo.custoUnitarioSnapshot);
}

/** Passo 2: soma das linhas, com o custo congelado de cada uma. */
export function custoMaterial(consumos: ConsumoCalculavel[]): Centavos {
  return consumos.reduce((total, c) => total + custoLinha(c), 0);
}

/** Passo 3: o passo que a maioria das artesãs esquece. */
export function custoMaoObra(horas: number, valorHora: Centavos): Centavos {
  return arredondaCentavos(horas * valorHora);
}

/** Passo 4: percentual sobre o custo direto, nunca sobre o preço. */
export function custoIndiretos(
  material: Centavos,
  maoObra: Centavos,
  percIndiretos: number,
): Centavos {
  return arredondaCentavos((material + maoObra) * percIndiretos);
}

/** Passo 6: markup sobre o custo total. */
export function aplicarLucro(custoTotal: Centavos, percLucro: number): Centavos {
  return arredondaCentavos(custoTotal * (1 + percLucro));
}

/**
 * Passo 7: a taxa de venda incide sobre o preço de venda, não sobre o custo.
 * Somar a taxa ao preço-base deixa a artesã no prejuízo — a operação é dividir.
 */
export function aplicarTaxas(precoBase: Centavos, percTaxas: number): Centavos {
  if (percTaxas >= 1) throw new Error('percTaxas deve ser menor que 100%.');
  return arredondaCentavos(precoBase / (1 - percTaxas));
}

/** Conversão oferecida na interface para quem pensa em margem, não em markup. */
export function margemParaMarkup(margem: number): number {
  if (margem >= 1) throw new Error('Margem deve ser menor que 100%.');
  return margem / (1 - margem);
}

// ------------------------------------------------------------------ cálculo

export function calcularPrecificacao(entrada: EntradaCalculo): Precificacao {
  const { horas, consumos, perfil } = entrada;

  const material = custoMaterial(consumos);
  const maoObra = custoMaoObra(horas, perfil.valorHora);
  const indiretos = custoIndiretos(material, maoObra, perfil.percIndiretos);
  const total = material + maoObra + indiretos;

  const precoBase = aplicarLucro(total, perfil.percLucro);
  const precoComTaxa = aplicarTaxas(precoBase, perfil.percTaxas);
  const precoSugerido = arredondaParaCima(precoComTaxa, perfil.arredondamento);

  const precoFinal = entrada.precoFinal ?? precoSugerido;

  const taxaValor = arredondaCentavos(precoFinal * perfil.percTaxas);
  const lucroLiquido = precoFinal - total - taxaValor;
  const margemReal = precoFinal > 0 ? lucroLiquido / precoFinal : 0;
  const retornoHora =
    horas > 0
      ? arredondaCentavos((precoFinal - material - indiretos - taxaValor) / horas)
      : 0;

  return {
    custoMaterial: material,
    custoMaoObra: maoObra,
    custoIndiretos: indiretos,
    custoTotal: total,
    precoBase,
    precoComTaxa,
    precoSugerido,
    precoFinal,
    taxaValor,
    lucroLiquido,
    margemReal,
    retornoHora,
    perfilSnapshot: { ...perfil },
    calculadoEm: new Date().toISOString(),
  };
}

// --------------------------------------------------------------- validações

export function validarPerfil(perfil: PerfilPrecificacao): string[] {
  const erros: string[] = [];
  if (perfil.valorHora <= 0) {
    erros.push('O valor da hora precisa ser maior que zero.');
  }
  if (
    perfil.percLucro < LIMITES.percLucro.min ||
    perfil.percLucro > LIMITES.percLucro.max
  ) {
    erros.push('O lucro precisa ficar entre 0% e 500%.');
  }
  if (
    perfil.percIndiretos < LIMITES.percIndiretos.min ||
    perfil.percIndiretos > LIMITES.percIndiretos.max
  ) {
    erros.push('Os custos indiretos precisam ficar entre 0% e 100%.');
  }
  if (
    perfil.percTaxas < LIMITES.percTaxas.min ||
    perfil.percTaxas > LIMITES.percTaxas.max
  ) {
    erros.push('As taxas de venda precisam ficar entre 0% e 50%.');
  }
  return erros;
}

export function validarEntrada(entrada: EntradaCalculo): string[] {
  const erros = validarPerfil(entrada.perfil);
  if (!(entrada.horas > 0)) {
    erros.push('Informe quantas horas a peça levou.');
  }
  entrada.consumos.forEach((c, i) => {
    if (!(c.quantidade > 0)) {
      erros.push(`A quantidade do material ${i + 1} precisa ser maior que zero.`);
    }
  });
  return erros;
}

export type Aviso = { tipo: 'atencao' | 'erro'; texto: string };

/** Avisos que a tela de preço mostra ao lado do resultado. */
export function avisos(
  precificacao: Precificacao,
  quantidadeDeMateriais: number,
): Aviso[] {
  const lista: Aviso[] = [];

  if (quantidadeDeMateriais === 0) {
    lista.push({
      tipo: 'atencao',
      texto:
        'Nenhum material lançado: o preço está saindo só da mão de obra. ' +
        'Adicione o fio e os aviamentos para o custo ficar completo.',
    });
  }

  if (precificacao.precoFinal < precificacao.custoTotal) {
    const falta = precificacao.custoTotal - precificacao.precoFinal;
    lista.push({
      tipo: 'erro',
      texto:
        'Esse preço está abaixo do custo. Você teria um prejuízo de ' +
        `R$ ${(falta / 100).toFixed(2).replace('.', ',')} nesta peça.`,
    });
  }

  return lista;
}
