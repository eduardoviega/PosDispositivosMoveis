package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.appcontatos.data.ContactDatasource
import br.edu.utfpr.appcontatos.data.ContactTypeEnum
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random

class ContactFormViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val contactId: Int = savedStateHandle.get<String>("id")?.toIntOrNull() ?: 0
    var uiState: ContactFormState by mutableStateOf(
        ContactFormState(
            contactId = contactId
        )
    )

    init {
        if (!uiState.isNewContact) {
            loadContact()
        }
    }

    fun loadContact() {
        uiState = uiState.copy(
            isLoading = true,
            hasErrorLoading = false
        )
        viewModelScope.launch {
            delay(2000)
            val contact = ContactDatasource.instance.findById(contactId)
            val hasError = Random.nextBoolean()
            uiState = if (contact == null || hasError) {
                uiState.copy(
                    isLoading = false,
                    hasErrorLoading = true
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    contact = contact,
                    formState = FormState(
                        firstName = FormField(contact.firstName),
                        lastName = FormField(contact.lastName),
                        phoneNumber = FormField(contact.phoneNumber),
                        email = FormField(contact.email),
                        isFavorite = FormField(contact.isFavorite),
                        assetValue = FormField(contact.assetValue.movePointRight(2).toString()),
                        birthDate = FormField(contact.birthDate),
                        type = FormField(contact.type)
                    )
                )
            }
        }
    }

    fun onFormEvent(event: FormEvent) {
        when (event) {
            is FormEvent.UpdateAssetValue -> onAssetValueChanged(event.newValue)
            is FormEvent.UpdateBirthDate -> onBirthDateChanged(event.newValue)
            is FormEvent.UpdateEmail -> onEmailChanged(event.newValue)
            is FormEvent.UpdateFirstName -> onFirstNameChanged(event.newValue)
            is FormEvent.UpdateIsFavorite -> onIsFavoriteChanged(event.newValue)
            is FormEvent.UpdateLastName -> onLastNameChanged(event.newValue)
            is FormEvent.UpdatePhoneNumber -> onPhoneNumberChanged(event.newValue)
            is FormEvent.UpdateType -> onTypeChanged(event.newValue)
        }
    }

    fun save() {
        if (uiState.isProcessing || !isValidForm()) return

        uiState = uiState.copy(
            isProcessing = true,
            processingErrorMessage = ""
        )
        viewModelScope.launch {
            delay(2000)
            val hasError = Random.nextBoolean()
            uiState = if (hasError) {
                uiState.copy(
                    isProcessing = false,
                    processingErrorMessage =
                        "Ocorreu um erro ao salvar. Aguarde um momento e tente novamente."
                )
            } else {
                val contactToSave = uiState.contact.copy(
                    firstName = uiState.formState.firstName.value,
                    lastName = uiState.formState.lastName.value,
                    phoneNumber = uiState.formState.phoneNumber.value,
                    email = uiState.formState.email.value,
                    isFavorite = uiState.formState.isFavorite.value,
                    assetValue = uiState.formState.assetValue.value.let {
                        if (it.isBlank()) {
                            BigDecimal.ZERO
                        } else {
                            BigDecimal(it).movePointLeft(2)
                        }
                    },
                    type = uiState.formState.type.value,
                    birthDate = uiState.formState.birthDate.value
                )
                ContactDatasource.instance.save(contactToSave)
                uiState.copy(
                    isProcessing = false,
                    contactUpdated = true
                )
            }
        }
    }

    fun showConfirmationDialog() {
        uiState = uiState.copy(showConfirmationDialog = true)
    }

    fun hideConfirmationDialog() {
        uiState = uiState.copy(showConfirmationDialog = false)
    }

    fun delete() {
        uiState = uiState.copy(
            isProcessing = true,
            showConfirmationDialog = false,
            processingErrorMessage = ""
        )
        viewModelScope.launch {
            delay(2000)
            val hasError = Random.nextBoolean()
            uiState = if (hasError) {
                uiState.copy(
                    isProcessing = false,
                    processingErrorMessage =
                        "Ocorreu um erro ao remover. Aguarde um momento e tente novamente."
                )
            } else {
                ContactDatasource.instance.delete(uiState.contact)
                uiState.copy(
                    isProcessing = false,
                    contactUpdated = true
                )
            }
        }
    }

    private fun onFirstNameChanged(newValue: String) {
        if (uiState.formState.firstName.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                firstName = FormField(
                    value = newValue,
                    errorMessage = validateFirstName(newValue)
                )
            )
        )
    }

    private fun validateFirstName(value: String): String =
        if (value.isBlank()) {
            "O nome é obrigatório"
        } else {
            ""
        }

    private fun onLastNameChanged(newValue: String) {
        if (uiState.formState.lastName.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                lastName = FormField(newValue)
            )
        )
    }

    private fun onPhoneNumberChanged(newValue: String) {
        val newPhoneNumber: String = newValue.filter { it.isDigit() }
        if (newPhoneNumber.length > 11
            || uiState.formState.phoneNumber.value == newPhoneNumber) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                phoneNumber = FormField(
                    value = newPhoneNumber,
                    errorMessage = validatePhoneNumber(newPhoneNumber)
                )
            )
        )
    }

    private fun validatePhoneNumber(value: String): String =
        if (value.isNotBlank() && value.length < 10) {
            "Informe um telefone válido"
        } else {
            ""
        }

    private fun onEmailChanged(newValue: String) {
        if (uiState.formState.email.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                email = FormField(
                    value = newValue,
                    errorMessage = validateEmail(newValue)
                )
            )
        )
    }

    private fun validateEmail(value: String): String =
        if (value.isNotBlank()
            && !Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matches(value)
        ) {
            "Informe um e-mail válido"
        } else {
            ""
        }

    private fun onIsFavoriteChanged(newValue: Boolean) {
        if (uiState.formState.isFavorite.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                isFavorite = FormField(newValue)
            )
        )
    }

    private fun onAssetValueChanged(newValue: String) {
        val newAssetValue = newValue.filter { it.isDigit() }
        if (newAssetValue.length >= 20
            || uiState.formState.assetValue.value == newAssetValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                assetValue = FormField(
                    value = newAssetValue
                )
            )
        )
    }

    private fun onBirthDateChanged(newValue: LocalDate) {
        if (uiState.formState.birthDate.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                birthDate = FormField(newValue)
            )
        )
    }

    private fun onTypeChanged(newValue: ContactTypeEnum) {
        if (uiState.formState.type.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                type = FormField(newValue)
            )
        )
    }

    private fun isValidForm(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                firstName = uiState.formState.firstName.copy(
                    errorMessage = validateFirstName(uiState.formState.firstName.value)
                ),
                phoneNumber = uiState.formState.phoneNumber.copy(
                    errorMessage = validatePhoneNumber(uiState.formState.phoneNumber.value)
                ),
                email = uiState.formState.email.copy(
                    errorMessage = validateEmail(uiState.formState.email.value)
                ),
            )
        )
        return uiState.formState.isValid
    }
}