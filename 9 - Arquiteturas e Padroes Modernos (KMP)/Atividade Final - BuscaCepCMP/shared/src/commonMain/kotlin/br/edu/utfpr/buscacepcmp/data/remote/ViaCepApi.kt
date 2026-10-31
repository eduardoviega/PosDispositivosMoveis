package br.edu.utfpr.buscacepcmp.data.remote

import br.edu.utfpr.buscacepcmp.data.model.EnderecoResponse

interface ViaCepApi {
    suspend fun buscarCep(cep: String): EnderecoResponse
}