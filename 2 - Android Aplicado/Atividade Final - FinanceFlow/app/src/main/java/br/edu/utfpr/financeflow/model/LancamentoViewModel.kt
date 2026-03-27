package br.edu.utfpr.financeflow.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.edu.utfpr.financeflow.enum.TipoLancamentoEnum
import java.time.LocalDateTime

class LancamentoViewModel: ViewModel() {
    val opcoes = TipoLancamentoEnum.entries.toTypedArray()
    var tipoSelecionado: TipoLancamentoEnum by mutableStateOf(opcoes[0])
        private set
    var descricao by mutableStateOf("")
        private set
    var valor by mutableDoubleStateOf(0.0)
        private set
    var data by mutableStateOf<LocalDateTime>(LocalDateTime.now())
        private set

    fun onTipoSelecionadoChange(newTipo: TipoLancamentoEnum) {
        tipoSelecionado = newTipo
    }

    fun onDescricaoChange(newDescricao: String) {
        descricao = newDescricao
    }

    fun onValorChange(newValor: Double) {
        valor = newValor
    }

    fun onDataChange(newData: LocalDateTime) {
        data = newData
    }
}