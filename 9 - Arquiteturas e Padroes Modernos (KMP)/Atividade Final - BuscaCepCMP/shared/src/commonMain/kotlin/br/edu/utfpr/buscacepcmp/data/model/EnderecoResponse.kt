package br.edu.utfpr.buscacepcmp.data.model

import br.edu.utfpr.buscacepcmp.domain.model.Endereco
import kotlinx.serialization.Serializable

@Serializable
data class EnderecoResponse(
    val cep: String? = null,
    val logradouro: String? = null,
    val bairro: String? = null,
    val localidade: String? = null,
    val uf: String? = null,
    val erro: String? = null
) {
    fun toDomain(): Endereco {
        return Endereco(
            cep = cep.orEmpty(),
            logradouro = logradouro.orEmpty(),
            bairro = bairro.orEmpty(),
            cidade = localidade.orEmpty(),
            uf = uf.orEmpty()
        )
    }
}