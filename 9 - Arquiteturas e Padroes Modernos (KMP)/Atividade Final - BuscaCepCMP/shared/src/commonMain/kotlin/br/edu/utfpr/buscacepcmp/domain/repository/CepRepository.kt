package br.edu.utfpr.buscacepcmp.domain.repository

import br.edu.utfpr.buscacepcmp.domain.model.Endereco

interface CepRepository {
    suspend fun buscarCep(cep: String): Result<Endereco>
}