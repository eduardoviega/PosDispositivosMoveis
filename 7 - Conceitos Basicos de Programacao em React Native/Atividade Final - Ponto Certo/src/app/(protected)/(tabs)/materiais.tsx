import { Link, useRouter } from 'expo-router';
import { useState } from 'react';
import { FlatList, Pressable, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { Botao, CampoBusca, Cartao, Etiqueta, EstadoVazio, Texto } from '@/components/ui';
import { ROTULO_CATEGORIA_MATERIAL } from '@/domain/regras';
import { custoUnitario, Material } from '@/domain/types';
import { combina } from '@/lib/busca';
import { formatarCentavos, formatarReais } from '@/lib/dinheiro';
import { useDados } from '@/state/dados';
import { esp, useCores } from '@/theme';

function LinhaMaterial({ material }: { material: Material }) {
  const unitario = custoUnitario(material);

  return (
    <Link href={{ pathname: '/material/[id]', params: { id: material.id } }} asChild>
      <Pressable>
        <Cartao estilo={{ gap: esp.sm }}>
          <View style={{ flexDirection: 'row', alignItems: 'flex-start', gap: esp.md }}>
            <View style={{ flex: 1, gap: 2 }}>
              <Texto variante="secao" numeroDeLinhas={1}>
                {material.nome}
              </Texto>
              <Texto variante="pequeno" cor="tinta2">
                {ROTULO_CATEGORIA_MATERIAL[material.categoria]}
                {material.cor ? ` · ${material.cor}` : ''} ·{' '}
                {material.qtdEmbalagem.toString().replace('.', ',')} {material.unidade} por{' '}
                {formatarReais(material.precoEmbalagem)}
              </Texto>
            </View>
            <View style={{ alignItems: 'flex-end' }}>
              <Texto variante="numero">R$ {formatarCentavos(unitario)}</Texto>
              <Texto variante="pequeno" cor="tinta3">
                por {material.unidade}
              </Texto>
            </View>
          </View>
          {material.arquivado ? <Etiqueta tom="neutro">arquivado</Etiqueta> : null}
        </Cartao>
      </Pressable>
    </Link>
  );
}

export default function TelaMateriais() {
  const { materiais } = useDados();
  const router = useRouter();
  const c = useCores();
  const [consulta, setConsulta] = useState('');

  const ordenados = [...materiais].sort((a, b) => {
    if (a.arquivado !== b.arquivado) return a.arquivado ? 1 : -1;
    return a.nome.localeCompare(b.nome, 'pt-BR');
  });

  const filtrados = ordenados.filter((m) =>
    combina(consulta, m.nome, ROTULO_CATEGORIA_MATERIAL[m.categoria], m.cor),
  );

  const irParaNovo = () =>
    router.push({ pathname: '/material/[id]', params: { id: 'novo' } });

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: c.fundo }} edges={['bottom']}>
      <FlatList
        data={filtrados}
        keyExtractor={(m) => m.id}
        contentContainerStyle={{ padding: esp.lg, gap: esp.md, paddingBottom: esp.xxl }}
        keyboardShouldPersistTaps="handled"
        renderItem={({ item }) => <LinhaMaterial material={item} />}
        ListHeaderComponent={
          materiais.length > 0 ? (
            <View style={{ gap: esp.md, marginBottom: esp.xs }}>
              <Botao titulo="Novo material" aoTocar={irParaNovo} />
              <CampoBusca valor={consulta} aoMudar={setConsulta} dica="Buscar material" />
            </View>
          ) : null
        }
        ListEmptyComponent={
          consulta.trim() !== '' ? (
            <EstadoVazio
              titulo="Nada encontrado"
              descricao={`Nenhum material bate com "${consulta.trim()}". Tente outro nome, categoria ou cor.`}
            />
          ) : (
            <EstadoVazio
              titulo="Seu armário está vazio"
              descricao={
                'Comece pelo que você usa mais: um novelo de fio. Cadastre o nome, ' +
                'quanto vem na embalagem (por exemplo 100 g) e quanto você pagou — ' +
                'o app calcula o custo por grama e usa isso em toda peça.'
              }
              acao={{ titulo: 'Cadastrar primeiro material', aoTocar: irParaNovo }}
            />
          )
        }
      />
    </SafeAreaView>
  );
}
