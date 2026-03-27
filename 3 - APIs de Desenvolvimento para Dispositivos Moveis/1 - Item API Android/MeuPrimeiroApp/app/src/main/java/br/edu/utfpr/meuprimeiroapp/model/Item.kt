package br.edu.utfpr.meuprimeiroapp.model

data class Item(
    val id: String,
    val value: ItemValue
)

data class ItemValue(
    val id: String,
    val name: String,
    val surname: String,
    val profession: String,
    val imageUrl: String,
    val age: Int,
    val location: ItemLocation?
) {
    val fullName: String
        get() = "$name $surname"
}

data class ItemLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)