package br.edu.utfpr.appcontatos.ui.contact.list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.appcontatos.data.Contact
import br.edu.utfpr.appcontatos.data.ContactDatasource
import br.edu.utfpr.appcontatos.data.groupByInitial
import kotlinx.coroutines.launch

class ContactsListViewModel : ViewModel() {
    val uiState: MutableState<ContactsListUiState> = mutableStateOf(ContactsListUiState())

    init {
        loadContacts()
    }

    fun loadContacts() {
        uiState.value = uiState.value.copy(
            isLoading = true,
            hasError = false
        )

        viewModelScope.launch {
            uiState.value = try {
                uiState.value.copy(
                    isLoading = false,
                    contacts = ContactDatasource.instance.findAll().groupByInitial()
                )
            } catch (ex: Exception) {
                Log.e("ContactsListViewModel", "Erro ao carregar contatos", ex)
                uiState.value.copy(
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
            uiState.value = uiState.value.copy(
                contacts = ContactDatasource.instance.findAll().groupByInitial()
            )
        } catch (ex: Exception) {
            Log.e("ContactsListViewModel", "Erro ao atualizar contato", ex)
        }
    }
}