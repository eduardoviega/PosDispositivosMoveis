package br.edu.utfpr.utfpr_car_api_android.service

import retrofit2.HttpException

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResult<T> {
    return try {
        val response = apiCall()
        ApiResult.Success(response)
    } catch (e: Exception) {
        when (e) {
            is HttpException -> {
                val code = e.code()
                val message = e.message()
                ApiResult.Error(code, message)
            }
            else -> {
                ApiResult.Error(-1, e.message ?: "Unknown error")
            }
        }
    }
}
