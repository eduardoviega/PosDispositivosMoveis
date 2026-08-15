import { styles } from "@/components/styles";
import { SuperButton } from "@/components/SuperButton";
import { SuperTextInput } from "@/components/SuperTextInput";
import { SuperTitle } from "@/components/SuperTitle";
import TodoItem, { ITodo } from "@/components/TodoItem";
import { useState } from "react";
import { Alert, ScrollView } from "react-native";

export default function Index() {

  const [newItem, setNewItem] = useState("");
  const [todos, setTodos] = useState<ITodo[]>([]);

  const addItem = () => {
    if (newItem.length < 4) return alert("O item deve ter no mínimo 4 caracteres");

    const item: ITodo = {
      id: Date.now().toString(),
      title: newItem,
      completed: false,
    };

    setTodos([item, ...todos]);
    setNewItem("");
  };

  const updateItem = (id: string) => {
    setTodos((todos) =>
      todos.map((todo) =>
        todo.id === id ? { ...todo, completed: !todo.completed } : todo
      )
    );
  }

  const deleteItem = (id: string) => {
    Alert.alert(
      "Excluir item",
      "Tem certeza que deseja excluir este item?",
      [
        {
          text: "Cancelar",
          style: "cancel",
        },
        {
          text: "Excluir",
          style: "destructive",
          onPress: () => {
            setTodos((todos) => todos.filter((todo) => todo.id !== id));
          },
        },
      ]
    );
  };

  return (
    <ScrollView style={styles.container}>
      <SuperTitle title="Lista de Compras" />
      <SuperTextInput value={newItem} onChangeText={setNewItem} />
      <SuperButton title="Novo Item" onPress={addItem} />

      {todos.map((todo) => {
        return (
          <TodoItem
            key={todo.id}
            todo={todo}
            updateItem={updateItem}
            deleteItem={deleteItem}
          />
        );
      })}
    </ScrollView>
  );
}
