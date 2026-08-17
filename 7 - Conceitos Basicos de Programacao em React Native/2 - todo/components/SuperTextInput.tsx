import { TextInput } from "react-native";
import { styles } from "./styles";

interface ISuperTextInput {
    value: string;
    onChangeText: (text: string) => void;
}

export function SuperTextInput({ value, onChangeText }: ISuperTextInput) {
    return (
        <TextInput
            style={styles.textInputContainer}
            value={value}
            onChangeText={onChangeText}
        />
    );
}