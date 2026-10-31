package br.edu.utfpr.buscacepcmp.domain.model

data class Endereco(
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val cidade: String,
    val uf: String
)
