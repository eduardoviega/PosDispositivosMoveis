package br.edu.utfpr.buscacepcmp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.buscacepcmp.domain.model.Endereco

@Composable
fun EnderecoCard(endereco: Endereco?) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Endereço:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            Text("CEP: ${showValue(endereco?.cep)}")
            Text("Logradouro: ${showValue(endereco?.logradouro)}")
            Text("Bairro: ${showValue(endereco?.bairro)}")
            Text("Cidade: ${showValue(endereco?.cidade)}")
            Text("UF: ${showValue(endereco?.uf)}")
        }
    }
}

private fun showValue(texto: String?): String {
    return texto?.ifEmpty { "Não encontrado" } ?: ""
}

@Preview
@Composable
private fun EnderecoCardPreview() {
    MaterialTheme {
        EnderecoCard(
            endereco = Endereco(
                cep = "01001-000",
                logradouro = "Praça da Sé",
                bairro = "Sé",
                cidade = "São Paulo",
                uf = "SP"
            )
        )
    }
}

@Preview
@Composable
private fun EnderecoCardEmptyPreview() {
    MaterialTheme {
        EnderecoCard(endereco = null)
    }
}
