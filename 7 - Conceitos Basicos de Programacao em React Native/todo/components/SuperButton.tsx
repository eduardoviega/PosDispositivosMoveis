import { Text, TouchableOpacity } from "react-native";
import { styles } from "./styles";

interface ISuperButton {
    title: string;
    onPress: () => void;
}

/**
 * Principal componente de botão da aplicação
 * @param title Título do botão
 * @param onPress Ação do botão clicado 
 * @returns um super botão
 */
export function SuperButton({ title, onPress }: ISuperButton) {
    return (
        <TouchableOpacity onPress={onPress} style={styles.containerButton}>
            <Text style={styles.titleButton}>{title}</Text>
        </TouchableOpacity>
    );
}