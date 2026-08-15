import { styles } from "@/components/styles";
import { SuperButton } from "@/components/SuperButton";
import { SuperTextInput } from "@/components/SuperTextInput";
import { SuperTitle } from "@/components/SuperTitle";
import TodoItem, { ITodoItem } from "@/components/TodoItem";
import { useState } from "react";
import { ScrollView } from "react-native";

export default function Index() {

  const [newItem, setNewItem] = useState("");
  const [todos, setTodos] = useState<ITodoItem[]>([]);

  const addItem = () => {
    if (newItem.length < 4) return alert("O item deve ter no mínimo 4 caracteres");

    const item: ITodoItem = {
      todo: {
        id: Date.now().toString(),
        title: newItem,
        completed: false,
      },
      updateItem: updateItem,
    };

    setTodos([item, ...todos]);
    setNewItem("");
  };

  const updateItem = (id: string) => {
    setTodos((prevTodos) => {
      return prevTodos.map((todoItem) => {
        if (todoItem.todo.id === id) {
          return {
            ...todoItem,
            todo: { ...todoItem.todo, completed: !todoItem.todo.completed },
          };
        }
        return todoItem;
      });
    });
  }

  return (
    <ScrollView style={styles.container}>
      <SuperTitle title="Lista de Compras" />
      <SuperTextInput value={newItem} onChangeText={setNewItem} />
      <SuperButton title="Novo Item" onPress={addItem} />

      {todos.map((todoItem) => {
        return <TodoItem
          key={todoItem.todo.id}
          todo={todoItem.todo}
          updateItem={updateItem}
        />
      })}
    </ScrollView>
  );
}
