package br.edu.utfpr.utfpr_car_api_android.service

import br.edu.utfpr.utfpr_car_api_android.model.ApiCarModel
import br.edu.utfpr.utfpr_car_api_android.model.CarModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CarApiService {

    @GET("car")
    suspend fun getCars(): List<CarModel>

    @GET("car/{id}")
    suspend fun getCar(@Path("id") id: String): ApiCarModel

    @DELETE("car/{id}")
    suspend fun deleteCar(@Path("id") id: String)

    @PATCH("car/{id}")
    suspend fun updateCar(@Path("id") id: String, @Body car: CarModel): CarModel

    @POST("car")
    suspend fun addCar(@Body car: CarModel): CarModel
}