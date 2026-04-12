package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import br.edu.utfpr.appcontatos.ui.contact.form.visualTransformation.CurrencyVisualTransformation
import br.edu.utfpr.appcontatos.ui.contact.form.visualTransformation.PhoneVisualTransformation
import br.edu.utfpr.appcontatos.ui.shared.composables.ContactAvatar
import br.edu.utfpr.appcontatos.ui.shared.composables.DefaultErrorState
import br.edu.utfpr.appcontatos.ui.shared.composables.DefaultLoadingState
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme


@Composable
fun ContactFormScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    viewModel: ContactFormViewModel = viewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onUpdated: () -> Unit
) {
    LaunchedEffect(viewModel.uiState.contactUpdated) {
        if (viewModel.uiState.contactUpdated) {
            onUpdated()
        }
    }

    LaunchedEffect(snackbarHostState, viewModel.uiState.processingErrorMessage) {
        if (viewModel.uiState.processingErrorMessage.isNotBlank()) {
            snackbarHostState.showSnackbar(viewModel.uiState.processingErrorMessage)
        }
    }

    if (viewModel.uiState.showConfirmationDialog) {
        ConfirmationDialog(
            content = "Esse contato será removido permanentemente",
            onDismiss = viewModel::hideConfirmationDialog,
            onConfirm = viewModel::delete
        )
    }


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
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                AppBar(
                    isNewContact = viewModel.uiState.isNewContact,
                    onBackPressed = onBackPressed,
                    isProcessing = viewModel.uiState.isProcessing,
                    onSavePressed = viewModel::save,
                    onDeletePressed = viewModel::showConfirmationDialog
                )
            }
        ) { paddingValues ->
            FormContent(
                modifier = Modifier.padding(paddingValues),
                formState = viewModel.uiState.formState,
                onFormEvent = viewModel::onFormEvent,
                isSaving = viewModel.uiState.isProcessing
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    isNewContact: Boolean,
    onBackPressed: () -> Unit,
    isProcessing: Boolean,
    onSavePressed: () -> Unit,
    onDeletePressed: () -> Unit
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
        },
        actions = {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                if (!isNewContact) {
                    IconButton(onClick = onDeletePressed) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remover"
                        )
                    }
                }
                IconButton(onClick = onSavePressed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Salvar"
                    )
                }
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
            onBackPressed = {},
            isProcessing = false,
            onSavePressed = {},
            onDeletePressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreviewSaving(
    @PreviewParameter(BooleanParameterProvider::class) isSaving: Boolean
) {
    AppContatosTheme {
        AppBar(
            isNewContact = true,
            onBackPressed = {},
            isProcessing = isSaving,
            onSavePressed = {},
            onDeletePressed = {}
        )
    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    formState: FormState,
    onFormEvent: (FormEvent) -> Unit,
    isSaving: Boolean
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
                keyboardCapitalization = KeyboardCapitalization.Words,
                enabled = !isSaving
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
                keyboardCapitalization = KeyboardCapitalization.Words,
                enabled = !isSaving
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
                keyboardType = KeyboardType.Phone,
                enabled = !isSaving,
                visualTransformation = PhoneVisualTransformation()
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
                keyboardType = KeyboardType.Email,
                enabled = !isSaving
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
                },
                enabled = !isSaving
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
                keyboardType = KeyboardType.Number,
                enabled = !isSaving,
                visualTransformation = CurrencyVisualTransformation()
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
                },
                enabled = !isSaving
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
                },
                enabled = !isSaving
            )
            FormRadioButton(
                modifier = choiceOptionsModifier,
                label = "Profissional",
                value = ContactTypeEnum.PROFESSIONAL,
                groupValue = formState.type.value,
                onValueChanged = { newValue ->
                    onFormEvent(FormEvent.UpdateType(newValue))
                },
                enabled = !isSaving
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
            onFormEvent = {},
            isSaving = false
        )
    }
}

@Composable
private fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        title = title?.let {
            { Text(it) }
        },
        text = { Text(content) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}