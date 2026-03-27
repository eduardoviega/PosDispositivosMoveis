package br.edu.utfpr.meuprimeiroapp.service

import br.edu.utfpr.meuprimeiroapp.model.Item
import br.edu.utfpr.meuprimeiroapp.model.ItemValue
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("items")
    suspend fun getItems(): List<Item>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): Item

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @PATCH("items/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body itemValue: ItemValue) : Item

    @POST("items")
    suspend fun createItem(@Body itemValue: ItemValue) : Item
}