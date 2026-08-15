import { StyleSheet } from "react-native";

export const theme = {
    background: "#0A0A0A",
    tint: "yellow",
    text: "white",
    button: "#333",
};

export const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: theme.background,
        padding: 16,
    },
    containerButton: {
        backgroundColor: theme.button,
        height: 60,
        alignSelf: "stretch",
        marginTop: 16,
        borderRadius: 16,
        alignItems: "center",
        justifyContent: "center",
    },
    titleButton: {
        fontSize: 20,
        fontWeight: "300",
        color: theme.text,
    },
    description: {
        fontSize: 14,
        fontWeight: "300",
        marginTop: 6,
        color: theme.button,
    },
    title: {
        fontSize: 30,
        fontWeight: "200",
        color: "white",
    },
    textInputContainer: {
        height: 50,
        alignSelf: "stretch",
        marginTop: 10,
        borderRadius: 16,
        paddingHorizontal: 16,
        backgroundColor: "#222",
        color: "white",
    },
    card: {
        justifyContent: "center",
        height: 80,
        paddingHorizontal: 16,
        alignSelf: "stretch",
        backgroundColor: "#111",
        borderRadius: 16,
        marginTop: 16,
    },
    checkbox: {
        position: "absolute",
        right: 16,
        width: 36,
        height: 36,
        alignItems: "center",
        justifyContent: "center",
        borderRadius: 8,
        backgroundColor: theme.button,
    },
});

