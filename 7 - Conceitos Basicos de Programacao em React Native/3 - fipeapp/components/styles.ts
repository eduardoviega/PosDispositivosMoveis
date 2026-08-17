import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
  item: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#FDfDfD",
    height: 50,
    borderBottomColor: "#333",
    borderBottomWidth: StyleSheet.hairlineWidth, // 1 = px / 2 = px / 3 = px / 4 = px
    paddingHorizontal: 16,
  },
  textInput: {
    height: 50,
    borderColor: "#333",
    borderWidth: StyleSheet.hairlineWidth,
    paddingHorizontal: 16,
    margin: 8,
    borderRadius: 8
  }
});

//100 imagens = 15mb * 100 =
// jpg = 3000 x 5000 x 3 = 15 mb de ram
// png = 1000 x 1000 x 4 = 4 mb de ram