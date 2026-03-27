package br.edu.utfpr.financeflow.enum

enum class TipoLancamentoEnum {
    CREDITO,
    DEBITO;

    fun getDescricao(): String {
        return when (this) {
            CREDITO -> "Crédito"
            DEBITO -> "Débito"
        }
    }
}