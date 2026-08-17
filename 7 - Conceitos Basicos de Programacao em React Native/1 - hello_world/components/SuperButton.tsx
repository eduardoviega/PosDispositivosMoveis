import { StyleSheet, Text, TouchableOpacity } from "react-native";

interface Props {
    title: string;
    uppercase?: boolean;
    onPress: () => void;
}

export function SuperButton(props: Props) {
    return (
        <TouchableOpacity style={styles.container} onPress={props.onPress}>
            <Text style={styles.title}>{props.title}</Text>
        </TouchableOpacity>
    );
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: "#333",
        height: 60,
        width: "90%",
        margin: 16,
        borderRadius: 16,
        alignItems: "center",
        justifyContent: "center",
    },
    title: {
        fontSize: 20,
        fontWeight: "bold",
        color: "white",
    }
});