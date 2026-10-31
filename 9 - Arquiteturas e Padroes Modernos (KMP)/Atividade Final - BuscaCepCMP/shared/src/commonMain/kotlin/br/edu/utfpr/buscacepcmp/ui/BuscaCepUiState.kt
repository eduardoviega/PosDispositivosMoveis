package br.edu.utfpr.buscacepcmp.ui

import br.edu.utfpr.buscacepcmp.domain.model.Endereco

sealed interface BuscaCepUiState {
    data object Loading : BuscaCepUiState

    data class Success(val endereco: Endereco?) : BuscaCepUiState

    data class Error(val message: String) : BuscaCepUiState
}