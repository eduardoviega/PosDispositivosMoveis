package br.edu.utfpr.utfpr_car_api_android.model

data class CarModel(
    val id: String,
    val imageUrl: String,
    val year: String,
    val name: String,
    val licence: String,
    val place: PlaceModel?,
)