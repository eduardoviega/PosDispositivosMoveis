package br.edu.utfpr.financeflow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.utfpr.financeflow.database.DatabaseHandler
import br.edu.utfpr.financeflow.entity.LancamentoEntity
import br.edu.utfpr.financeflow.enum.TipoLancamentoEnum
import br.edu.utfpr.financeflow.model.LancamentoViewModel
import br.edu.utfpr.financeflow.ui.theme.FinanceFlowTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceFlowTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier,
                    enterTransition = {
                        slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                    },
                    exitTransition = {
                        slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    }
                ) {
                    composable("home") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            CadastroScreen(
                                modifier = Modifier.padding(innerPadding),
                                onNavigateToLancamentos = {
                                    navController.navigate("lancamentos")
                                }
                            )
                        }
                    }
                    composable("lancamentos") {
                        ExtratoScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CadastroScreen(
    modifier: Modifier = Modifier,
    viewModel: LancamentoViewModel = viewModel(),
    onNavigateToLancamentos: () -> Unit,
) {
    val context = LocalContext.current
    val banco = DatabaseHandler.getInstance(LocalContext.current)

    fun onClickSalvar() {
        var msg = ""
        if (viewModel.descricao.isEmpty()) {
            msg = "Preencha todos os campos!"
        } else if (viewModel.valor <= 0) {
            msg = "O valor deve ser maior que zero!"
        }

        if (msg.isNotEmpty()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            return@onClickSalvar
        }

        val lancamentoEntity = LancamentoEntity(
            _id = 0,
            tipoSelecionado = viewModel.tipoSelecionado,
            descricao = viewModel.descricao,
            valor = viewModel.valor,
            data = viewModel.data
        )
        banco.inserir(lancamentoEntity)

        viewModel.onDescricaoChange("")
        viewModel.onValorChange(0.0)
        viewModel.onDataChange(LocalDateTime.now())

        Toast.makeText(
            context,
            "Lançamento inserido com sucesso!",
            Toast.LENGTH_SHORT
        ).show()
    }

    Column(modifier = modifier) {
        CampoTipoPagamento(
            viewModel.opcoes,
            viewModel.tipoSelecionado,
            { viewModel.onTipoSelecionadoChange(it) }
        )
        CampoDescricao(viewModel.descricao, { viewModel.onDescricaoChange(it) })
        CampoValor(viewModel.valor, { viewModel.onValorChange(it) })
        DateTimePicker(viewModel.data, { viewModel.onDataChange(it) })
        Botoes({ onClickSalvar() }, onNavigateToLancamentos)
    }
}

@Preview(showBackground = true)
@Composable
fun CadastroScreenPreview() {
    FinanceFlowTheme {
        CadastroScreen(
            onNavigateToLancamentos = { }
        )
    }
}

@Composable
fun Botoes(
    onClickSalvar: () -> Unit,
    onNavigateToLancamentos: () -> Unit
) {
    Row {
        Button(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            onClick = onClickSalvar,
        ) { Text("Salvar") }
        Button(
            onClick = onNavigateToLancamentos,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) { Text("Ver Lançamentos") }
    }
}

@Preview
@Composable
fun BotoesPreview() {
    FinanceFlowTheme {
        Botoes(
            onClickSalvar = { },
            onNavigateToLancamentos = { }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTipoPagamento(
    opcoes: Array<TipoLancamentoEnum>,
    tipoSelecionado: TipoLancamentoEnum,
    onChangeTipoSelecionado: (TipoLancamentoEnum) -> Unit = {}
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            value = tipoSelecionado.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo do lançamento") },
            placeholder = { Text("Selecione") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(opcao.toString()) },
                    onClick = {
                        onChangeTipoSelecionado(opcao)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampoTipoPagamentoPreview() {
    FinanceFlowTheme {
        CampoTipoPagamento(
            opcoes = TipoLancamentoEnum.entries.toTypedArray(),
            tipoSelecionado = TipoLancamentoEnum.CREDITO
        )
    }
}

@Composable
fun CampoDescricao(descricao: String, onChangeDescricao: (String) -> Unit = {}) {
    Column {
        OutlinedTextField(
            value = descricao,
            onValueChange = { onChangeDescricao(it) },
            label = { Text("Informe a descrição do lançamento") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoDescricaoPreview() {
    FinanceFlowTheme {
        CampoDescricao(
            descricao = "Salário"
        )
    }
}

@Composable
fun CampoValor(valor: Double, onChangeValorNumerico: (Double) -> Unit = {}) {
    var valorTexto by rememberSaveable { mutableStateOf(if (valor == 0.0) "" else valor.toString()) }

    // Sincroniza quando o valor do ViewModel muda
    LaunchedEffect(valor) {
        val formatted = if (valor == 0.0) "" else valor.toString()
        if (formatted != valorTexto) valorTexto = formatted
    }

    Column {
        OutlinedTextField(
            value = valorTexto,
            onValueChange = { novoTexto ->
                if (novoTexto.matches(Regex("^\\d*\\.?\\d*$"))) {
                    valorTexto = novoTexto
                    onChangeValorNumerico(novoTexto.toDouble())
                }
            },
            label = { Text("Informe o valor do lançamento") },
            prefix = { Text("R$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoValorPreview() {
    FinanceFlowTheme {
        CampoValor(
            valor = 100.0
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    dateTime: LocalDateTime?,
    onDateTimeChange: (LocalDateTime) -> Unit,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedTime by rememberSaveable { mutableStateOf<LocalTime?>(null) }

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val texto = dateTime?.format(formatter) ?: ""
    val label = "Data e hora"

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { showDatePicker = true }
    ) {
        OutlinedTextField(
            value = texto,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label) },
            placeholder = { Text("Selecionar") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        selectedDate = Instant
                            .ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        showDatePicker = false
                        showTimePicker = true
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()

        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(
                        timePickerState.hour,
                        timePickerState.minute
                    )

                    val finalDateTime = LocalDateTime.of(
                        selectedDate!!,
                        selectedTime!!
                    )

                    onDateTimeChange(finalDateTime)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Selecione o horário") }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateTimePickerPreview() {
    FinanceFlowTheme {
        DateTimePicker(
            dateTime = LocalDateTime.now(),
            onDateTimeChange = {}
        )
    }
}
