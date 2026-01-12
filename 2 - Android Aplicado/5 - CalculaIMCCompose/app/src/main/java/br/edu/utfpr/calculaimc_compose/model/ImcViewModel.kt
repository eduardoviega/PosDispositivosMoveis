package br.edu.utfpr.calculaimc_compose.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel

class ImcViewModel : ViewModel() {
    var peso by mutableStateOf("")
        private set
    var altura by mutableStateOf("")
        private set
    var resultado by mutableStateOf("0.0")
        private set

    fun onPesoChange(newPeso: String) {
        peso = newPeso
    }

    fun onAlturaChange(newAltura: String) {
        altura = newAltura
    }

    fun calcularIMC() {
        val pesoValor = peso.toDoubleOrNull()
        val alturaValor = altura.toDoubleOrNull()

        if (pesoValor != null && alturaValor != null && alturaValor != 0.0) {
            val imc = pesoValor / (alturaValor * alturaValor)
            resultado = String.format("%.2f", imc)
        }
    }

    fun limparCampos() {
        peso = ""
        altura = ""
        resultado = "0.0"
    }
}