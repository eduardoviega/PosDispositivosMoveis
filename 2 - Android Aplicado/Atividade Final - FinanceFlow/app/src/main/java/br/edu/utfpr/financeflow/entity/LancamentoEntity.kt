package br.edu.utfpr.financeflow.entity

import br.edu.utfpr.financeflow.enum.TipoLancamentoEnum
import java.time.LocalDateTime

data class LancamentoEntity(
    val _id: Int,
    val tipoSelecionado: TipoLancamentoEnum,
    val descricao: String,
    val valor: Double?,
    val data: LocalDateTime
)
