/**
 * Kit de interface do Ponto Certo. Componentes pequenos, todos lendo a paleta
 * de `@/theme`, para nenhuma tela precisar inventar cor ou espaçamento.
 */

import Ionicons from '@expo/vector-icons/Ionicons';
import { ReactNode, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Keyboard,
  Pressable,
  ScrollView,
  StyleProp,
  Text,
  TextInput,
  TextStyle,
  View,
  ViewStyle,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { Cores, esp, raio, tipo, useCores } from '@/theme';

// ------------------------------------------------------------------- layout

export function Tela({
  children,
  rolavel = true,
  estilo,
}: {
  children: ReactNode;
  rolavel?: boolean;
  estilo?: StyleProp<ViewStyle>;
}) {
  const c = useCores();
  const conteudo = (
    <View style={[{ padding: esp.lg, gap: esp.lg }, estilo]}>{children}</View>
  );

  // No Android com edge-to-edge (SDK 54), nada redimensiona a tela sozinho
  // quando o teclado abre. Reservar a altura dele como padding do scroll é
  // que garante dar pra rolar até o último campo — sem KeyboardAvoidingView
  // junto, que faria a mesma reserva duas vezes.
  const [alturaTeclado, setAlturaTeclado] = useState(0);
  useEffect(() => {
    const mostrar = Keyboard.addListener('keyboardDidShow', (e) =>
      setAlturaTeclado(e.endCoordinates.height),
    );
    const esconder = Keyboard.addListener('keyboardDidHide', () => setAlturaTeclado(0));
    return () => {
      mostrar.remove();
      esconder.remove();
    };
  }, []);

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: c.fundo }} edges={['bottom']}>
      {rolavel ? (
        <ScrollView
          keyboardShouldPersistTaps="handled"
          contentContainerStyle={{ paddingBottom: esp.xxl + alturaTeclado }}>
          {conteudo}
        </ScrollView>
      ) : (
        conteudo
      )}
    </SafeAreaView>
  );
}

export function Cartao({
  children,
  estilo,
}: {
  children: ReactNode;
  estilo?: StyleProp<ViewStyle>;
}) {
  const c = useCores();
  return (
    <View
      style={[
        {
          backgroundColor: c.superficie,
          borderColor: c.linha,
          borderWidth: 1,
          borderRadius: raio.lg,
          padding: esp.lg,
          gap: esp.md,
        },
        estilo,
      ]}>
      {children}
    </View>
  );
}

// -------------------------------------------------------------------- texto

type TextoProps = {
  children: ReactNode;
  variante?: keyof typeof tipo;
  cor?: keyof Cores;
  estilo?: StyleProp<TextStyle>;
  numeroDeLinhas?: number;
};

export function Texto({
  children,
  variante = 'corpo',
  cor = 'tinta',
  estilo,
  numeroDeLinhas,
}: TextoProps) {
  const c = useCores();
  const valor = c[cor];
  return (
    <Text
      numberOfLines={numeroDeLinhas}
      style={[tipo[variante], { color: typeof valor === 'string' ? valor : c.tinta }, estilo]}>
      {children}
    </Text>
  );
}

export function Rotulo({ children }: { children: ReactNode }) {
  return (
    <Texto variante="rotulo" cor="tinta3" estilo={{ textTransform: 'uppercase' }}>
      {children}
    </Texto>
  );
}

export function Etiqueta({
  children,
  tom = 'acento',
}: {
  children: ReactNode;
  tom?: 'acento' | 'positivo' | 'atencao' | 'erro' | 'neutro';
}) {
  const c = useCores();
  const mapa = {
    acento: [c.acentoLavado, c.acento],
    positivo: [c.positivoLavado, c.positivo],
    atencao: [c.atencaoLavado, c.atencao],
    erro: [c.erroLavado, c.erro],
    neutro: [c.superficieAlt, c.tinta2],
  } as const;
  const [fundo, texto] = mapa[tom];
  return (
    <View
      style={{
        backgroundColor: fundo,
        borderRadius: raio.sm,
        paddingHorizontal: esp.sm,
        paddingVertical: 3,
        alignSelf: 'flex-start',
      }}>
      <Text style={[tipo.rotulo, { color: texto, textTransform: 'uppercase' }]}>
        {children}
      </Text>
    </View>
  );
}

// ------------------------------------------------------------------ entrada

export function Campo({
  rotulo,
  valor,
  aoMudar,
  dica,
  sufixo,
  teclado = 'default',
  multilinha = false,
  erro,
}: {
  rotulo: string;
  valor: string;
  aoMudar: (v: string) => void;
  dica?: string;
  sufixo?: string;
  teclado?: 'default' | 'decimal-pad';
  multilinha?: boolean;
  erro?: string;
}) {
  const c = useCores();
  return (
    <View style={{ gap: esp.xs }}>
      <Rotulo>{rotulo}</Rotulo>
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          backgroundColor: c.superficie,
          borderColor: erro ? c.erro : c.linha,
          borderWidth: 1,
          borderRadius: raio.md,
          paddingHorizontal: esp.md,
        }}>
        <TextInput
          value={valor}
          onChangeText={aoMudar}
          placeholder={dica}
          placeholderTextColor={c.tinta3}
          keyboardType={teclado}
          multiline={multilinha}
          style={{
            flex: 1,
            color: c.tinta,
            paddingVertical: esp.md,
            minHeight: multilinha ? 90 : 44,
            textAlignVertical: multilinha ? 'top' : 'center',
            ...tipo.corpo,
          }}
        />
        {sufixo ? <Texto cor="tinta3">{sufixo}</Texto> : null}
      </View>
      {erro ? (
        <Texto variante="pequeno" cor="erro">
          {erro}
        </Texto>
      ) : null}
    </View>
  );
}

export function CampoBusca({
  valor,
  aoMudar,
  dica = 'Buscar',
}: {
  valor: string;
  aoMudar: (v: string) => void;
  dica?: string;
}) {
  const c = useCores();
  return (
    <View
      style={{
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: c.superficie,
        borderColor: c.linha,
        borderWidth: 1,
        borderRadius: raio.md,
        paddingHorizontal: esp.md,
        gap: esp.sm,
      }}>
      <Ionicons name="search-outline" size={18} color={c.tinta3} />
      <TextInput
        value={valor}
        onChangeText={aoMudar}
        placeholder={dica}
        placeholderTextColor={c.tinta3}
        autoCorrect={false}
        autoCapitalize="none"
        accessibilityLabel={dica}
        style={{
          flex: 1,
          color: c.tinta,
          paddingVertical: esp.md,
          ...tipo.corpo,
        }}
      />
      {valor ? (
        <Pressable
          onPress={() => aoMudar('')}
          accessibilityRole="button"
          accessibilityLabel="Limpar busca"
          hitSlop={8}>
          <Ionicons name="close-circle" size={18} color={c.tinta3} />
        </Pressable>
      ) : null}
    </View>
  );
}

export function Selecao<T extends string | number>({
  rotulo,
  opcoes,
  valor,
  aoMudar,
  formatar,
}: {
  rotulo?: string;
  opcoes: readonly T[];
  valor: T;
  aoMudar: (v: T) => void;
  formatar?: (v: T) => string;
}) {
  const c = useCores();
  return (
    <View style={{ gap: esp.xs }}>
      {rotulo ? <Rotulo>{rotulo}</Rotulo> : null}
      <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: esp.sm }}>
        {opcoes.map((o) => {
          const ativo = o === valor;
          return (
            <Pressable
              key={String(o)}
              onPress={() => aoMudar(o)}
              accessibilityRole="button"
              accessibilityState={{ selected: ativo }}
              style={{
                paddingHorizontal: esp.md,
                paddingVertical: esp.sm,
                borderRadius: raio.pilula,
                borderWidth: 1,
                borderColor: ativo ? c.acento : c.linha,
                backgroundColor: ativo ? c.acentoLavado : c.superficie,
              }}>
              <Text
                style={[
                  tipo.pequeno,
                  { color: ativo ? c.acento : c.tinta2, fontWeight: ativo ? '700' : '400' },
                ]}>
                {formatar ? formatar(o) : o}
              </Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

// ------------------------------------------------------------------- botões

export function Botao({
  titulo,
  aoTocar,
  variante = 'primario',
  carregando = false,
  desabilitado = false,
}: {
  titulo: string;
  aoTocar: () => void;
  variante?: 'primario' | 'secundario' | 'perigo';
  carregando?: boolean;
  desabilitado?: boolean;
}) {
  const c = useCores();
  const inativo = desabilitado || carregando;

  const estilos = {
    primario: { fundo: c.acento, texto: c.superficie, borda: c.acento },
    secundario: { fundo: 'transparent', texto: c.acento, borda: c.acento },
    perigo: { fundo: 'transparent', texto: c.erro, borda: c.erro },
  }[variante];

  return (
    <Pressable
      onPress={aoTocar}
      disabled={inativo}
      accessibilityRole="button"
      style={({ pressed }) => ({
        backgroundColor: estilos.fundo,
        borderColor: estilos.borda,
        borderWidth: 1,
        borderRadius: raio.md,
        paddingVertical: esp.md,
        paddingHorizontal: esp.lg,
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: 46,
        opacity: inativo ? 0.5 : pressed ? 0.85 : 1,
      })}>
      {carregando ? (
        <ActivityIndicator color={estilos.texto} />
      ) : (
        <Text style={[tipo.corpo, { color: estilos.texto, fontWeight: '700' }]}>{titulo}</Text>
      )}
    </Pressable>
  );
}

// ------------------------------------------------------------------- avisos

export function Aviso({
  tom,
  children,
}: {
  tom: 'atencao' | 'erro' | 'positivo' | 'neutro';
  children: ReactNode;
}) {
  const c = useCores();
  const mapa = {
    atencao: [c.atencaoLavado, c.atencao],
    erro: [c.erroLavado, c.erro],
    positivo: [c.positivoLavado, c.positivo],
    neutro: [c.superficieAlt, c.tinta2],
  } as const;
  const [fundo, borda] = mapa[tom];
  return (
    <View
      style={{
        backgroundColor: fundo,
        borderLeftColor: borda,
        borderLeftWidth: 3,
        borderRadius: raio.sm,
        padding: esp.md,
      }}>
      <Text style={[tipo.pequeno, { color: borda }]}>{children}</Text>
    </View>
  );
}

export function EstadoVazio({
  titulo,
  descricao,
  acao,
}: {
  titulo: string;
  descricao: string;
  acao?: { titulo: string; aoTocar: () => void };
}) {
  const c = useCores();
  return (
    <View
      style={{
        alignItems: 'center',
        gap: esp.md,
        paddingVertical: esp.xxl,
        paddingHorizontal: esp.lg,
        borderRadius: raio.lg,
        borderWidth: 1,
        borderStyle: 'dashed',
        borderColor: c.linha,
      }}>
      <Texto variante="secao" estilo={{ textAlign: 'center' }}>
        {titulo}
      </Texto>
      <Texto variante="pequeno" cor="tinta2" estilo={{ textAlign: 'center' }}>
        {descricao}
      </Texto>
      {acao ? (
        <View style={{ alignSelf: 'stretch', marginTop: esp.sm }}>
          <Botao titulo={acao.titulo} aoTocar={acao.aoTocar} />
        </View>
      ) : null}
    </View>
  );
}

export function LinhaValor({
  rotulo,
  valor,
  destaque = false,
  cor,
}: {
  rotulo: string;
  valor: string;
  destaque?: boolean;
  cor?: keyof Cores;
}) {
  const c = useCores();
  return (
    <View
      style={{
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'baseline',
        gap: esp.md,
        paddingVertical: destaque ? esp.sm : 2,
        borderTopWidth: destaque ? 1 : 0,
        borderTopColor: c.linha,
      }}>
      <Text
        style={[
          destaque ? tipo.numero : tipo.pequeno,
          { color: destaque ? c.tinta : c.tinta2, flexShrink: 1 },
        ]}>
        {rotulo}
      </Text>
      <Text
        style={[
          tipo.numero,
          {
            color: cor ? (c[cor] as string) : destaque ? c.tinta : c.tinta,
            fontWeight: destaque ? '700' : '600',
            fontVariant: ['tabular-nums'],
          },
        ]}>
        {valor}
      </Text>
    </View>
  );
}

export function Separador() {
  const c = useCores();
  return <View style={{ height: 1, backgroundColor: c.linha }} />;
}
