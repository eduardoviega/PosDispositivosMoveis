/**
 * Barra de composição do preço — o elemento de identidade do app.
 *
 * Mostra em uma olhada a proporção entre material, trabalho, indiretos, lucro
 * e taxa dentro do preço final. É o que a tabela leva dez segundos para
 * explicar.
 */

import { View } from 'react-native';

import { Precificacao } from '@/domain/types';
import { formatarPercentual, formatarReais } from '@/lib/dinheiro';
import { esp, raio, useCores } from '@/theme';
import { Rotulo, Texto } from './ui';

type Fatia = { nome: string; valor: number; cor: string };

export function BarraComposicao({ p }: { p: Precificacao }) {
  const c = useCores();

  const fatias: Fatia[] = [
    { nome: 'Material', valor: p.custoMaterial, cor: c.fatias[0] },
    { nome: 'Trabalho', valor: p.custoMaoObra, cor: c.fatias[1] },
    { nome: 'Indiretos', valor: p.custoIndiretos, cor: c.fatias[2] },
    { nome: 'Lucro', valor: Math.max(p.lucroLiquido, 0), cor: c.fatias[3] },
    { nome: 'Taxa', valor: p.taxaValor, cor: c.fatias[4] },
  ].filter((f) => f.valor > 0);

  const total = fatias.reduce((s, f) => s + f.valor, 0);
  if (total <= 0) return null;

  return (
    <View style={{ gap: esp.md }}>
      <Rotulo>Onde vai o preço</Rotulo>

      <View
        style={{
          flexDirection: 'row',
          height: 14,
          borderRadius: raio.pilula,
          overflow: 'hidden',
          backgroundColor: c.superficieAlt,
        }}>
        {fatias.map((f) => (
          <View key={f.nome} style={{ flex: f.valor / total, backgroundColor: f.cor }} />
        ))}
      </View>

      <View style={{ gap: esp.xs }}>
        {fatias.map((f) => (
          <View
            key={f.nome}
            style={{ flexDirection: 'row', alignItems: 'center', gap: esp.sm }}>
            <View
              style={{
                width: 10,
                height: 10,
                borderRadius: 3,
                backgroundColor: f.cor,
              }}
            />
            <Texto variante="pequeno" cor="tinta2" estilo={{ flex: 1 }}>
              {f.nome}
            </Texto>
            <Texto variante="pequeno" cor="tinta3">
              {formatarPercentual(f.valor / total, 0)}
            </Texto>
            <Texto variante="pequeno" estilo={{ width: 92, textAlign: 'right' }}>
              {formatarReais(f.valor)}
            </Texto>
          </View>
        ))}
      </View>

      {p.lucroLiquido < 0 ? (
        <Texto variante="pequeno" cor="erro">
          O preço não cobre o custo, então não há fatia de lucro nesta barra.
        </Texto>
      ) : null}
    </View>
  );
}
