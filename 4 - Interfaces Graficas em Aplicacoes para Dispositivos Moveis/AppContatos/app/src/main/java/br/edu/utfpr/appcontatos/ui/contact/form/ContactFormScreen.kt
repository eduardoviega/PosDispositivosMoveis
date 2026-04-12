package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme

@Composable
fun ContactFormScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(
                isNewContact = true,
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->
        Text(
            text = "Alterar",
            modifier = Modifier.padding(paddingValues)
        )
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