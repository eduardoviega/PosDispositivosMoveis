import Ionicons from '@expo/vector-icons/Ionicons';
import { Tabs } from 'expo-router';

import { useCores } from '@/theme';

export default function TabsLayout() {
  const c = useCores();

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
          title: 'Projetos',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="grid-outline" color={color} size={size} />
          ),
        }}
      />
      <Tabs.Screen
        name="materiais"
        options={{
          title: 'Materiais',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="cube-outline" color={color} size={size} />
          ),
        }}
      />
      <Tabs.Screen
        name="ideias"
        options={{
          title: 'Ideias',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="bulb-outline" color={color} size={size} />
          ),
        }}
      />
      <Tabs.Screen
        name="ajustes"
        options={{
          title: 'Ajustes',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="settings-outline" color={color} size={size} />
          ),
        }}
      />
    </Tabs>
  );
}
