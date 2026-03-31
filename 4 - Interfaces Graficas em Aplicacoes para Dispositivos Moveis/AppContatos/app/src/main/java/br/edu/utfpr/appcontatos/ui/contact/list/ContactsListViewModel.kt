package br.edu.utfpr.appcontatos.ui.contact.list

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.appcontatos.data.Contact
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

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
            delay(2000)
            val hasError = Random.nextBoolean()

            uiState.value = if (hasError) {
                uiState.value.copy(
                    hasError = true,
                    isLoading = false
                )
            } else {
                val isEmpty = Random.nextBoolean()
                if (isEmpty) {
                    uiState.value.copy(
                    contacts = listOf(),
                        isLoading = false

                    )
                } else {
                    uiState.value.copy(
                        contacts = generateContacts(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleIsFavorite(updatedContact: Contact) {
        uiState.value = uiState.value.copy(
            contacts = uiState.value.contacts.map { contact ->
                if (contact.id == updatedContact.id) {
                    contact.copy(isFavorite = !contact.isFavorite)
                } else {
                    contact
                }
            }
        )
    }
}