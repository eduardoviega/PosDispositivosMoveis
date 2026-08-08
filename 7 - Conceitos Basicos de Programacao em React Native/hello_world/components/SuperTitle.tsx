import { StyleSheet, Text } from "react-native";

interface Props {
    title: string;
    uppercase?: boolean;
}

export function SuperTitle(props: Props) {
    let content = props.title;

    if (props.uppercase) {
        content = content.toUpperCase();
    }

    return (
        <Text style={styles.title}>{content}</Text>
    );
}

const styles = StyleSheet.create({
    title: {
        fontSize: 30,
        fontWeight: "bold",
        color: "white",
    }
});