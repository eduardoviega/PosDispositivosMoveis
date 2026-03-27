package br.edu.utfpr.financeflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import br.edu.utfpr.financeflow.database.DatabaseHandler
import br.edu.utfpr.financeflow.entity.LancamentoEntity
import br.edu.utfpr.financeflow.enum.TipoLancamentoEnum
import br.edu.utfpr.financeflow.ui.theme.FinanceFlowTheme
import java.time.format.DateTimeFormatter

class ExtratoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinanceFlowTheme {
                ExtratoScreen(
                    navController = NavHostController(this),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtratoScreen(navController: NavHostController) {
    val banco = DatabaseHandler.getInstance(LocalContext.current)

    var listaFiltrada by remember { mutableStateOf<List<LancamentoEntity>>(emptyList()) }
    var filtroTipo by remember { mutableStateOf<TipoLancamentoEnum?>(null) }
    var totalReceita by remember { mutableDoubleStateOf(0.0) }
    var totalDespesa by remember { mutableDoubleStateOf(0.0) }
    var saldo by remember { mutableDoubleStateOf(0.0) }

    fun updateTotalReceita() {
        totalReceita = listaFiltrada
            .filter { it.tipoSelecionado == TipoLancamentoEnum.CREDITO }
            .sumOf { it.valor ?: 0.0 }
    }

    fun updateTotalDespesa() {
        totalDespesa = listaFiltrada
            .filter { it.tipoSelecionado == TipoLancamentoEnum.DEBITO }
            .sumOf { it.valor ?: 0.0 }
    }

    fun updateResumo() {
        updateTotalReceita()
        updateTotalDespesa()
        saldo = totalReceita - totalDespesa
    }

    fun updateList(tipoLancamentoEnum: TipoLancamentoEnum?) {
        listaFiltrada = banco.listar(tipoLancamentoEnum)
        updateResumo()
    }

    updateList(filtroTipo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fluxo de Caixa") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
        ) {
            FiltrosLancamentos(
                filtroTipo = filtroTipo,
                onFiltroTipoChange = {
                    filtroTipo = it
                    updateList(tipoLancamentoEnum = it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResumoFinanceiro(
                receita = totalReceita,
                despesa = totalDespesa,
                saldo = saldo
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaFiltrada) { lancamentoEntity ->
                    LancamentoCard(lancamentoEntity)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExtratoScreenPreview() {
    FinanceFlowTheme {
        ExtratoScreen(
            navController = NavHostController(LocalContext.current),
        )
    }
}

@Composable
fun LancamentoCard(lancamentoEntity: LancamentoEntity) {

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val cor = if (lancamentoEntity.tipoSelecionado == TipoLancamentoEnum.CREDITO) {
        Color(0xFF2E7D32)
    } else {
        Color(0xFFC62828)
    }

    fun getDataAndHour(): String {
        val dataList = lancamentoEntity.data.format(formatter).split(" ")
        return dataList[0] + "\n às " + dataList[1]
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = getDataAndHour(),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lancamentoEntity.tipoSelecionado.getDescricao(),
                    color = cor,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lancamentoEntity.descricao,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(2f),
                    maxLines = (4)
                )

                Text(
                    text = "R$ ${"%.2f".format(lancamentoEntity.valor)}",
                    color = cor,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun LancamentoCardPreview() {
    FinanceFlowTheme {
        LancamentoCard(
            lancamentoEntity = LancamentoEntity(
                _id = 1,
                tipoSelecionado = TipoLancamentoEnum.CREDITO,
                descricao = "Salário",
                valor = 2500.0,
                data = java.time.LocalDateTime.now()
            )
        )
    }
}

@Composable
fun ResumoFinanceiro(
    receita: Double,
    despesa: Double,
    saldo: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Resumo", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Receitas: R$ ${"%.2f".format(receita)}",
                color = Color(0xFF2E7D32)
            )
            Text(
                text = "Despesas: R$ ${"%.2f".format(despesa)}",
                color = Color(0xFFC62828)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            Text(
                text = "Saldo: R$ ${"%.2f".format(saldo)}",
                style = MaterialTheme.typography.titleLarge,
                color = if (saldo >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResumoFinanceiroPreview() {
    FinanceFlowTheme {
        ResumoFinanceiro(
            receita = 1050.0,
            despesa = 500.0,
            saldo = 550.0,
        )
    }
}

@Composable
fun TextLabel(text: String) {
    FinanceFlowTheme {
        Text(text, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
fun FiltrosLancamentos(
    filtroTipo: TipoLancamentoEnum?,
    onFiltroTipoChange: (TipoLancamentoEnum?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        FilterChip(
            selected = filtroTipo == null,
            onClick = { onFiltroTipoChange(null) },
            shape = RoundedCornerShape(16.dp),
            label = { TextLabel("Todos") },
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = filtroTipo == TipoLancamentoEnum.CREDITO,
            onClick = { onFiltroTipoChange(TipoLancamentoEnum.CREDITO) },
            shape = RoundedCornerShape(16.dp),
            label = { TextLabel("Receitas") },
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = filtroTipo == TipoLancamentoEnum.DEBITO,
            onClick = { onFiltroTipoChange(TipoLancamentoEnum.DEBITO) },
            shape = RoundedCornerShape(16.dp),
            label = { TextLabel("Despesas") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FiltrosLancamentosPreview() {
    FinanceFlowTheme {
        FiltrosLancamentos(
            filtroTipo = TipoLancamentoEnum.CREDITO,
            onFiltroTipoChange = {}
        )
    }
}