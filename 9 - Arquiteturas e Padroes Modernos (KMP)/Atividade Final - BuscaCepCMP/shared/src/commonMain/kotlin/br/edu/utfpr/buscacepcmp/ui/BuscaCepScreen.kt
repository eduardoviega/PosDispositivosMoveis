package br.edu.utfpr.buscacepcmp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.buscacepcmp.domain.model.Endereco
import br.edu.utfpr.buscacepcmp.ui.components.EnderecoCard

@Composable
fun BuscaCepScreen(
    modifier: Modifier = Modifier,
    viewModel: BuscaCepViewModel = viewModel { BuscaCepViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cepInput by viewModel.cepInput.collectAsStateWithLifecycle()

    BuscaCepScreen(
        modifier = modifier,
        uiState = uiState,
        cepInput = cepInput,
        onCepChange = viewModel::onCepChange,
        buscarCep = viewModel::buscarCep,
    )
}

@Composable
private fun BuscaCepScreen(
    modifier: Modifier = Modifier,
    uiState: BuscaCepUiState,
    cepInput: String,
    onCepChange: (String) -> Unit,
    buscarCep: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is BuscaCepUiState.Error -> {
                snackbarHostState.showSnackbar(uiState.message)
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Busca de CEP"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                OutlinedTextField(
                    value = cepInput,
                    onValueChange = { onCepChange(it) },
                    label = { Text("Digite o CEP") },
                    enabled = uiState !is BuscaCepUiState.Loading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Search
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            buscarCep()
                        }
                    ),
                )

                Spacer(modifier = Modifier.height(12.dp))

                ElevatedButton(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        buscarCep()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is BuscaCepUiState.Loading && cepInput.length == 8,
                ) {
                    if (uiState is BuscaCepUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                        return@ElevatedButton
                    }
                    Text(text = "Buscar")
                }

                Spacer(modifier = Modifier.height(12.dp))

                EnderecoCard(
                    endereco = when (uiState) {
                        is BuscaCepUiState.Success -> uiState.endereco
                        else -> null
                    }
                )

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BuscaCepScreenEmptyPreview() {
    MaterialTheme {
        BuscaCepScreen(
            uiState = BuscaCepUiState.Success(endereco = null),
            cepInput = "",
            onCepChange = {},
            buscarCep = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BuscaCepScreenLoadingPreview() {
    MaterialTheme {
        BuscaCepScreen(
            uiState = BuscaCepUiState.Loading,
            cepInput = "01001-000",
            onCepChange = {},
            buscarCep = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BuscaCepScreenSuccessPreview() {
    MaterialTheme {
        BuscaCepScreen(
            uiState = BuscaCepUiState.Success(
                endereco = Endereco(
                    cep = "01001-000",
                    logradouro = "Praça da Sé",
                    bairro = "Sé",
                    cidade = "São Paulo",
                    uf = "SP"
                ),
            ),
            cepInput = "01001000",
            onCepChange = {},
            buscarCep = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BuscaCepScreenErrorPreview() {
    MaterialTheme {
        BuscaCepScreen(
            uiState = BuscaCepUiState.Error(
                "Ocorreu um erro ao consultar o CEP. Aguarde um momento e tente novamente."
            ),
            cepInput = "01001000",
            onCepChange = {},
            buscarCep = {},
        )
    }
}
