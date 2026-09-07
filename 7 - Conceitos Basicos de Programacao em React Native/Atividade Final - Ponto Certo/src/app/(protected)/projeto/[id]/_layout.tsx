import Ionicons from '@expo/vector-icons/Ionicons';
import { Tabs, useGlobalSearchParams } from 'expo-router';

import { useCores } from '@/theme';

/**
 * Sub-abas da peça. Preço e Anúncio ficam sem link na barra enquanto a peça
 * é só um rascunho (id "novo") — ainda não existe o que precificar ou
 * anunciar até o primeiro "Salvar".
 */
export default function ProjetoLayout() {
  const { id } = useGlobalSearchParams<{ id: string }>();
  const c = useCores();
  const novo = id === 'novo';

  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: c.acento,
        tabBarInactiveTintColor: c.tinta3,
        tabBarStyle: { backgroundColor: c.superficie, borderTopColor: c.linha },
        headerStyle: { backgroundColor: c.superficie },
        headerTitleStyle: { color: c.tinta, fontWeight: '700' },
        headerTintColor: c.acento,
      }}>
      <Tabs.Screen
        name="index"
        options={{
          title: 'Detalhes',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="document-text-outline" color={color} size={size} />
          ),
        }}
      />
      <Tabs.Screen
        name="preco"
        options={{
          title: 'Preço',
          href: novo ? null : undefined,
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="pricetag-outline" color={color} size={size} />
          ),
        }}
      />
      <Tabs.Screen
        name="anuncio"
        options={{
          title: 'Anúncio',
          href: novo ? null : undefined,
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="megaphone-outline" color={color} size={size} />
          ),
        }}
      />
    </Tabs>
  );
}
