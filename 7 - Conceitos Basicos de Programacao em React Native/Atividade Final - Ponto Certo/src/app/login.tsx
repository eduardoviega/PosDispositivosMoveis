import { Redirect } from 'expo-router';
import { useState } from 'react';
import { Alert, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { Botao, Texto } from '@/components/ui';
import { entrarComGoogle } from '@/service/auth/cliente';
import { useSessao } from '@/state/auth';
import { esp, useCores } from '@/theme';

export default function TelaLogin() {
  const c = useCores();
  const { usuario } = useSessao();
  const [entrando, setEntrando] = useState(false);

  // Sessão já ativa (ex.: acabou de logar, ou voltou com sessão persistida) —
  // não faz sentido mostrar o botão de novo.
  if (usuario) return <Redirect href="/" />;

  async function tocarEntrar() {
    setEntrando(true);
    try {
      // Se a artesã cancelar o seletor de conta, `entrarComGoogle` só devolve
      // `false` — não é erro. Se entrar, `useSessao` atualiza e o redirect
      // no topo deste componente troca de tela sozinho.
      await entrarComGoogle();
    } catch (erro) {
      // Diagnóstico temporário — ver o texto exato no terminal do Metro.
      console.error('Falha no login com Google:', erro);
      const codigo =
        typeof erro === 'object' && erro !== null && 'code' in erro
          ? String((erro as { code: unknown }).code)
          : undefined;
      const mensagem = erro instanceof Error ? erro.message : String(erro);
      Alert.alert('Não foi possível entrar', codigo ? `[${codigo}] ${mensagem}` : mensagem);
    } finally {
      setEntrando(false);
    }
  }

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: c.fundo }}>
      <View
        style={{
          flex: 1,
          justifyContent: 'center',
          alignItems: 'center',
          gap: esp.xxl,
          padding: esp.xl,
        }}>
        <View style={{ alignItems: 'center', gap: esp.sm }}>
          <Texto variante="destaque" cor="acento">
            Ponto Certo
          </Texto>
          <Texto variante="corpo" cor="tinta2" estilo={{ textAlign: 'center' }}>
            Calculadora de preço para quem faz crochê.
          </Texto>
        </View>

        <View style={{ alignSelf: 'stretch' }}>
          <Botao
            titulo={entrando ? 'Entrando…' : 'Entrar com o Google'}
            aoTocar={tocarEntrar}
            carregando={entrando}
          />
        </View>
      </View>
    </SafeAreaView>
  );
}
