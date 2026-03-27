package br.edu.utfpr.meuprimeiroapp.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Use plain HTTP for local development (10.0.2.2 forwards to host machine emulator)
    // The TLS error "Unable to parse TLS packet header" happens when the client
    // tries to perform an HTTPS/TLS handshake but the server is speaking plain HTTP.
    // Switch to http:// when your dev server is not configured with TLS.
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}