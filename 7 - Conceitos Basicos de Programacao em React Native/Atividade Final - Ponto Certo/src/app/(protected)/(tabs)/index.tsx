import { Image } from 'expo-image';
import { Link, useRouter } from 'expo-router';
import { useState } from 'react';
import { ActivityIndicator, FlatList, Pressable, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { Botao, CampoBusca, Cartao, Etiqueta, EstadoVazio, Texto } from '@/components/ui';
import { custoDesatualizado, ROTULO_CATEGORIA_PROJETO } from '@/domain/regras';
import { Projeto } from '@/domain/types';
import { combina } from '@/lib/busca';
import { formatarReais } from '@/lib/dinheiro';
import { paraFonteImagem } from '@/lib/foto';
import { useDados } from '@/state/dados';
import { esp, raio, useCores } from '@/theme';

function CartaoProjeto({ projeto, desatualizado }: { projeto: Projeto; desatualizado: boolean }) {
  const c = useCores();

  return (
    <Link href={{ pathname: '/projeto/[id]', params: { id: projeto.id } }} asChild>
      <Pressable>
        <Cartao estilo={{ gap: esp.md }}>
          <View style={{ flexDirection: 'row', gap: esp.md, alignItems: 'center' }}>
            {projeto.fotoThumb ? (
              <Image
                source={paraFonteImagem(projeto.fotoThumb)}
                style={{ width: 56, height: 56, borderRadius: raio.md }}
                contentFit="cover"
              />
            ) : (
              <View
                style={{
                  width: 56,
                  height: 56,
                  borderRadius: raio.md,
                  backgroundColor: c.superficieAlt,
                  alignItems: 'center',
                  justifyContent: 'center',
                }}>
                <Texto variante="pequeno" cor="tinta3">
                  sem foto
                </Texto>
              </View>
            )}

            <View style={{ flex: 1, gap: 2 }}>
              <Texto variante="secao" numeroDeLinhas={1}>
                {projeto.nome}
              </Texto>
              <Texto variante="pequeno" cor="tinta2">
                {ROTULO_CATEGORIA_PROJETO[projeto.categoria]} ·{' '}
                {projeto.horas.toString().replace('.', ',')} h ·{' '}
                {projeto.consumos.length}{' '}
                {projeto.consumos.length === 1 ? 'material' : 'materiais'}
              </Texto>
            </View>

            <View style={{ alignItems: 'flex-end', gap: 2 }}>
              {projeto.precificacao ? (
                <>
                  <Texto variante="numero">
                    {formatarReais(projeto.precificacao.precoFinal)}
                  </Texto>
                  <Texto variante="pequeno" cor="tinta3">
                    {formatarReais(projeto.precificacao.retornoHora)}/h
                  </Texto>
                </>
              ) : (
                <Texto variante="pequeno" cor="tinta3">
                  sem preço
                </Texto>
              )}
            </View>
          </View>

          {desatualizado ? (
            <Etiqueta tom="atencao">custo desatualizado</Etiqueta>
          ) : projeto.status === 'vendido' ? (
            <Etiqueta tom="positivo">vendido</Etiqueta>
          ) : null}
        </Cartao>
      </Pressable>
    </Link>
  );
}

export default function TelaProjetos() {
  const { projetos, materiais, carregando } = useDados();
  const router = useRouter();
  const c = useCores();
  const [consulta, setConsulta] = useState('');

  const filtrados = projetos.filter((p) =>
    combina(consulta, p.nome, ROTULO_CATEGORIA_PROJETO[p.categoria]),
  );

  if (carregando) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', backgroundColor: c.fundo }}>
        <ActivityIndicator color={c.acento} />
      </View>
    );
  }

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: c.fundo }} edges={['bottom']}>
      <FlatList
        data={filtrados}
        keyExtractor={(p) => p.id}
        contentContainerStyle={{ padding: esp.lg, gap: esp.md, paddingBottom: esp.xxl }}
        keyboardShouldPersistTaps="handled"
        renderItem={({ item }) => (
          <CartaoProjeto
            projeto={item}
            desatualizado={custoDesatualizado(item, materiais)}
          />
        )}
        ListHeaderComponent={
          projetos.length > 0 ? (
            <View style={{ gap: esp.md, marginBottom: esp.xs }}>
              <Botao
                titulo="Nova peça"
                aoTocar={() => router.push({ pathname: '/projeto/[id]', params: { id: 'novo' } })}
              />
              <CampoBusca valor={consulta} aoMudar={setConsulta} dica="Buscar peça" />
            </View>
          ) : null
        }
        ListEmptyComponent={
          consulta.trim() !== '' ? (
            <EstadoVazio
              titulo="Nada encontrado"
              descricao={`Nenhuma peça bate com "${consulta.trim()}". Tente outro nome ou categoria.`}
            />
          ) : (
            <EstadoVazio
              titulo="Nenhuma peça ainda"
              descricao={
                'Cadastre a peça que você acabou de fazer: nome, horas de trabalho e os ' +
                'materiais gastos. O preço sai calculado, com o custo detalhado.'
              }
              acao={{
                titulo: 'Cadastrar primeira peça',
                aoTocar: () =>
                  router.push({ pathname: '/projeto/[id]', params: { id: 'novo' } }),
              }}
            />
          )
        }
      />
    </SafeAreaView>
  );
}
