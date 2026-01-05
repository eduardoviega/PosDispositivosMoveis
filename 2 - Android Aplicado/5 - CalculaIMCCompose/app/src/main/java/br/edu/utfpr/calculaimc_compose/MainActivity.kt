package br.edu.utfpr.calculaimc_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.calculaimc_compose.ui.theme.CalculaIMCComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculaIMCComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculaIMCScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculaIMCScreen(modifier: Modifier = Modifier) {
    var peso by rememberSaveable { mutableStateOf("") }
    var altura by rememberSaveable { mutableStateOf("") }
    var resultado by rememberSaveable { mutableStateOf("") }

    val calcularIMC = {
        val pesoValor = peso.toDoubleOrNull()
        val alturaValor = altura.toDoubleOrNull()

        if (pesoValor != null && alturaValor != null && alturaValor != 0.0) {
            val imc = pesoValor / (alturaValor * alturaValor)
            resultado = String.format("%.2f", imc)
        }
    }

    Column {
        Text(
            text = "Peso:",
            modifier = Modifier.padding(8.dp, 48.dp, 8.dp, 8.dp)
        )
        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Informe seu peso em kg") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )

        Text(
            text = "Altura:",
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = altura,
            onValueChange = { altura = it },
            label = { Text("Informe sua altura em metros") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )

        Text(
            text = "Resultado:",
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = resultado,
            modifier = Modifier.padding(8.dp)
        )

        Row {
            Button(
                onClick = calcularIMC,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text("Calcular IMC")
            }
            Button(
                onClick = { peso = ""; altura = ""; resultado = "0.0" },
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text("Limpar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculaIMCComposeTheme {
        CalculaIMCScreen()
    }
}