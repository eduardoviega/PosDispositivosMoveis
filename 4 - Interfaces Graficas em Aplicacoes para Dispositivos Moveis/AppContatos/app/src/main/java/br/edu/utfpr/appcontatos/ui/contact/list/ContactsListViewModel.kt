package br.edu.utfpr.appcontatos.ui.contact.list

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.appcontatos.data.Contact
import br.edu.utfpr.appcontatos.data.ContactDatasource
import br.edu.utfpr.appcontatos.data.groupByInitial
import kotlinx.coroutines.launch

class ContactsListViewModel : ViewModel() {
    var uiState: ContactsListUiState by mutableStateOf(ContactsListUiState())
        private set

    init {
        loadContacts()
    }

    fun loadContacts() {
        uiState = uiState.copy(
            isLoading = true,
            hasError = false
        )

        viewModelScope.launch {
            uiState = try {
                uiState.copy(
                    isLoading = false,
                    contacts = ContactDatasource.instance.findAll().groupByInitial()
                )
            } catch (ex: Exception) {
                Log.e("ContactsListViewModel", "Erro ao carregar contatos", ex)
                uiState.copy(
                    isLoading = false,
                    hasError = true
                )
            }
        }
    }

    fun toggleIsFavorite(contact: Contact) {
        try {
            val updatedContact = contact.copy(isFavorite = !contact.isFavorite)
            ContactDatasource.instance.save(updatedContact)
            uiState = uiState.copy(
                contacts = ContactDatasource.instance.findAll().groupByInitial()
            )
        } catch (ex: Exception) {
            Log.e("ContactsListViewModel", "Erro ao atualizar contato", ex)
        }
    }
}