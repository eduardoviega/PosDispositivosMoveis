package br.edu.utfpr.calculaimc_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.utfpr.calculaimc_compose.model.ImcViewModel
import br.edu.utfpr.calculaimc_compose.ui.theme.CalculaIMCComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculaIMCComposeTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding),
                        enterTransition = {
                            slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                        },
                        exitTransition = {
                            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                        }
                    ) {
                        composable("home") {
                            CalculaIMCScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigateToDeveloper = {
                                    navController.navigate("developer")
                                }
                            )
                        }
                        composable("developer") {
                            DeveloperScreen(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculaIMCScreen(
    modifier: Modifier = Modifier,
    viewModel: ImcViewModel = viewModel(),
    onNavigateToDeveloper: () -> Unit
) {
    var focusRequester by remember { mutableStateOf(FocusRequester()) }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = viewModel.peso,
            onValueChange = { viewModel.onPesoChange(it) },
            label = { Text("Informe seu peso em kg") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.altura,
            onValueChange = { viewModel.onAlturaChange(it) },
            label = { Text("Informe sua altura em metros") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (viewModel.resultado.toDouble() > 0) {
            PanelResult(resultado = viewModel.resultado)
        }

        PanelButtons(
            onCalcularClick = { viewModel.calcularIMC() },
            onLimparClick = {
                viewModel.limparCampos()
                focusRequester.requestFocus()
            },
            modifier = modifier,
        )

        Button(
            onClick = onNavigateToDeveloper,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Sobre o Desenvolvedor")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculaIMCScreenPreview() {
    CalculaIMCComposeTheme {
        CalculaIMCScreen(
            onNavigateToDeveloper = {}
        )
    }
}

@Composable
fun PanelResult(resultado: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resultado:",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = resultado,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
private fun PanelResultPreview() {
    CalculaIMCComposeTheme {
        PanelResult(resultado = "22.5")
    }
}

@Composable
fun PanelButtons(
    onCalcularClick: () -> Unit,
    onLimparClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onCalcularClick,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) {
            Text("Calcular IMC")
        }
        Button(
            onClick = onLimparClick,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Limpar")
        }
    }
}

@Preview
@Composable
private fun PanelButtonsPreview() {
    CalculaIMCComposeTheme {
        PanelButtons(
            onCalcularClick = {},
            onLimparClick = {}
        )
    }
}

@Composable
fun DeveloperScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Desenvolvido por:")
        Text(
            "eduardoviega12@gmail.com", style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview
@Composable
private fun DeveloperScreenPreview() {
    CalculaIMCComposeTheme {
        DeveloperScreen()
    }
}