package br.edu.utfpr.buscacepcmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.buscacepcmp.data.repository.CepRepositoryImpl
import br.edu.utfpr.buscacepcmp.domain.repository.CepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuscaCepViewModel(
    private val repository: CepRepository = CepRepositoryImpl()
) : ViewModel() {
    private val _uiState = MutableStateFlow<BuscaCepUiState>(BuscaCepUiState.Success(
        endereco = null
    ))
    val uiState: StateFlow<BuscaCepUiState> = _uiState.asStateFlow()

    private val _cepInput = MutableStateFlow("")
    val cepInput: StateFlow<String> = _cepInput.asStateFlow()

    fun onCepChange(newCep: String) {
        val sanitized = newCep.filter { it.isDigit() }.take(8)
        _cepInput.value = sanitized
    }

    fun buscarCep() {
        val cep = _cepInput.value.trim()
        _uiState.value = BuscaCepUiState.Loading

        if (cep.length != 8) {
            _uiState.value = BuscaCepUiState.Error("Digite um CEP válido com 8 dígitos.")
            return
        }

        viewModelScope.launch {
            repository.buscarCep(cep)
                .onSuccess { endereco ->
                    _uiState.value = BuscaCepUiState.Success(endereco)
                }
                .onFailure { exception ->
                    _uiState.value = BuscaCepUiState.Error(
                        exception.message ?: "Ocorreu um erro ao consultar o CEP."
                    )
                }
        }
    }
}