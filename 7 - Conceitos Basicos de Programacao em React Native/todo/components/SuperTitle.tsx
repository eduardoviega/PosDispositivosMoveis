import { Text } from "react-native";
import { styles } from "./styles";

interface Props {
    title: string;
    uppercase?: boolean;
}

/**
 * Título estilizado para a aplicação
 * @param title Texto do título
 * @param uppercase Se o título deve ser exibido em maiúsculas
 * @returns Um título estilizado
 */
export function SuperTitle(props: Props) {
    let content = props.title;

    if (props.uppercase) {
        content = content.toUpperCase();
    }

    return (
        <Text style={styles.title}>{content}</Text>
    );
}