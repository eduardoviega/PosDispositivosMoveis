package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.appcontatos.data.ContactTypeEnum
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormCheckbox
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormDatePicker
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormFieldRow
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormRadioButton
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormTextField
import br.edu.utfpr.appcontatos.ui.shared.composables.ContactAvatar
import br.edu.utfpr.appcontatos.ui.shared.composables.DefaultErrorState
import br.edu.utfpr.appcontatos.ui.shared.composables.DefaultLoadingState
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme

@Composable
fun ContactFormScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    viewModel: ContactFormViewModel = viewModel()
) {
    val contentModifier: Modifier = modifier.fillMaxSize()
    if (viewModel.uiState.isLoading) {
        DefaultLoadingState(modifier = contentModifier)
    } else if (viewModel.uiState.hasErrorLoading) {
        DefaultErrorState(
            modifier = contentModifier,
            onTryAgainPressed = viewModel::loadContact
        )
    } else {
        Scaffold(
            modifier = contentModifier,
            topBar = {
                AppBar(
                    isNewContact = true,
                    onBackPressed = onBackPressed
                )
            }
        ) { paddingValues ->
            FormContent(
                modifier = Modifier.padding(paddingValues),
                formState = viewModel.uiState.formState,
                onFormEvent = viewModel::onFormEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    isNewContact: Boolean,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(if (isNewContact) "Novo Contato" else "Editar Contato")
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        }
    )
}

private class BooleanParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview(
    @PreviewParameter(BooleanParameterProvider::class) isNewContact: Boolean
) {
    AppContatosTheme {
        AppBar(
            isNewContact = isNewContact,
            onBackPressed = {}
        )
    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    formState: FormState,
    onFormEvent: (FormEvent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val formTextFieldModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ContactAvatar(
            modifier = Modifier.padding(16.dp),
            firstName = formState.firstName.value,
            lastName = formState.lastName.value,
            size = 150.dp,
            textStyle = MaterialTheme.typography.displayLarge
        )
        FormFieldRow(
            label = "Nome",
            imageVector = Icons.Filled.Person
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Nome",
                value = formState.firstName.value,
                errorMessage = formState.firstName.errorMessage,
                onValueChange = { newValue ->
                    onFormEvent(FormEvent.UpdateFirstName(newValue))
                },
                keyboardCapitalization = KeyboardCapitalization.Words
            )
        }
        FormFieldRow(
            label = "Sobrenome"
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Sobrenome",
                value = formState.lastName.value,
                errorMessage = formState.lastName.errorMessage,
                onValueChange = { newValue ->
                    onFormEvent(FormEvent.UpdateLastName(newValue))
                },
                keyboardCapitalization = KeyboardCapitalization.Words
            )
        }
        FormFieldRow(
            label = "Telefone",
            imageVector = Icons.Filled.Phone
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Telefone",
                value = formState.phoneNumber.value,
                errorMessage = formState.phoneNumber.errorMessage,
                onValueChange = { newValue ->
                    onFormEvent(FormEvent.UpdatePhoneNumber(newValue))
                },
                keyboardType = KeyboardType.Phone
            )
        }
        FormFieldRow(
            label = "E-mail",
            imageVector = Icons.Filled.Mail
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "E-mail",
                value = formState.email.value,
                errorMessage = formState.email.errorMessage,
                onValueChange = { newValue ->
                    onFormEvent(FormEvent.UpdateEmail(newValue))
                },
                keyboardType = KeyboardType.Email
            )
        }
        FormFieldRow(
            label = "Data de aniversário"
        ) {
            FormDatePicker(
                modifier = formTextFieldModifier,
                label = "Data de aniversário",
                value = formState.birthDate.value,
                errorMessage = formState.birthDate.errorMessage,
                onValueChange = { newValue ->
                    onFormEvent(FormEvent.UpdateBirthDate(newValue))
                }
            )
        }
        FormFieldRow(
            label = "Valor patrimonial",
            imageVector = Icons.Filled.AttachMoney
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Valor patrimonial",
                value = formState.assetValue.value,
                errorMessage = formState.assetValue.errorMessage,
                onValueChange = { newValue ->
                    onFormEvent(FormEvent.UpdateAssetValue(newValue))
                },
                keyboardType = KeyboardType.Number
            )
        }
        val choiceOptionsModifier = Modifier.padding(8.dp)
        FormFieldRow(
            label = "Favorito"
        ) {
            FormCheckbox(
                modifier = choiceOptionsModifier,
                label = "Favorito",
                checked = formState.isFavorite.value,
                onCheckedChange = { newValue ->
                    onFormEvent(FormEvent.UpdateIsFavorite(newValue))
                }
            )
        }
        FormFieldRow(
            label = "Tipo"
        ) {
            FormRadioButton(
                modifier = choiceOptionsModifier,
                label = "Pessoal",
                value = ContactTypeEnum.PERSONAL,
                groupValue = formState.type.value,
                onValueChanged = { newValue ->
                    onFormEvent(FormEvent.UpdateType(newValue))
                }
            )
            FormRadioButton(
                modifier = choiceOptionsModifier,
                label = "Profissional",
                value = ContactTypeEnum.PROFESSIONAL,
                groupValue = formState.type.value,
                onValueChanged = { newValue ->
                    onFormEvent(FormEvent.UpdateType(newValue))
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormContentPreview() {
    AppContatosTheme {
        FormContent(
            formState = FormState(),
            onFormEvent = {}
        )
    }
}