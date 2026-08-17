import Ionicons from "@expo/vector-icons/Ionicons";
import { Text, TouchableOpacity, View } from "react-native";
import { styles } from "./styles";

export interface ITodo {
    id: string;
    title: string;
    completed: boolean;
}

export interface ITodoItem {
    todo: ITodo;
    updateItem: (id: string) => void;
    deleteItem: (id: string) => void;
}

export default function TodoItem({ todo, updateItem, deleteItem }: ITodoItem) {
    const updateTodo = () => {
        updateItem(todo.id);
    }

    const deleteTodo = () => {
        deleteItem(todo.id);
    }

    return (
        <TouchableOpacity
            style={styles.card}
            onPress={updateTodo}
            onLongPress={deleteTodo}
        >
            <Text style={styles.titleButton}>{todo.title}</Text>
            <Text style={styles.description}>
                {todo.completed
                    ? "Concluída"
                    : "Toque para concluir · segure para excluir"}
            </Text>

            <View style={styles.checkbox}>
                {todo.completed && (
                    <Ionicons name="checkmark" size={32} color="white" />
                )}
            </View>
        </TouchableOpacity>
    );
}