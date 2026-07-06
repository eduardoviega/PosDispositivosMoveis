package br.edu.utfpr.climaambiente.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import br.edu.utfpr.climaambiente.model.AmbienteStatus
import br.edu.utfpr.climaambiente.model.AmbienteUiState
import br.edu.utfpr.climaambiente.viewmodel.ConfortoAmbientalViewModel
import kotlin.math.roundToInt

/**
 * Tela principal do aplicativo (camada de UI do MVVM).
 *
 * Responsabilidades:
 * - Obter a [ConfortoAmbientalViewModel] e **observar** o [AmbienteUiState] de forma reativa.
 * - Controlar o ciclo de vida do monitoramento dos sensores com [LaunchedEffect] (início) e
 *   [DisposableEffect] (limpeza), padrão recomendado para sensores em Compose.
 *
 * A tela não contém regra de negócio, apenas desenha o estado recebido.
 */
@Composable
fun ConfortoAmbientalScreen(
    viewModel: ConfortoAmbientalViewModel = viewModel()
) {
    // Observa o StateFlow da ViewModel de forma consciente do ciclo de vida.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // LaunchedEffect: registra os listeners dos sensores assim que a tela entra em composição.
    LaunchedEffect(Unit) {
        viewModel.iniciarMonitoramento()
    }

    // DisposableEffect: garante que os listeners sejam removidos quando a tela sai de composição.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.pararMonitoramento()
        }
    }

    ConfortoAmbientalContent(uiState = uiState)
}

@Composable
private fun ConfortoAmbientalContent(uiState: AmbienteUiState) {
    // Cor de fundo que muda conforme o status.
    val corFundo by animateColorAsState(
        targetValue = corDoStatus(uiState.status),
        animationSpec = tween(durationMillis = 600),
        label = "corFundo"
    )
    val corConteudo = corDoConteudo(uiState.status)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(corFundo)
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título fixo do aplicativo.
            Text(
                text = "Clima Ambiente",
                color = corConteudo,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            // Ícone grande que representa visualmente o status atual.
            Text(
                text = iconeDoStatus(uiState.status),
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )

            // Card com as leituras dos sensores.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LeituraSensor(
                    titulo = "Temperatura:",
                    valor = textoTemperatura(uiState),
                    cor = corConteudo
                )
                LeituraSensor(
                    titulo = "Umidade:",
                    valor = textoUmidade(uiState),
                    cor = corConteudo
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(1.dp)
                    .background(corConteudo.copy(alpha = 0.4f))
            )

            // Status calculado (título) e descrição.
            Text(
                text = uiState.tituloStatus,
                color = corConteudo,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = uiState.descricaoStatus,
                color = corConteudo,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(1.dp)
                    .background(corConteudo.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
private fun LeituraSensor(
    titulo: String,
    valor: String,
    cor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            color = cor,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = valor,
            color = cor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

private fun textoTemperatura(uiState: AmbienteUiState): String = when {
    !uiState.temperaturaDisponivel -> "Sensor de temperatura indisponível."
    !uiState.possuiLeituraTemperatura -> "-- °C"
    else -> "${uiState.temperatura.roundToInt()}°C"
}

private fun textoUmidade(uiState: AmbienteUiState): String = when {
    !uiState.umidadeDisponivel -> "Sensor de umidade indisponível."
    !uiState.possuiLeituraUmidade -> "-- %"
    else -> "${uiState.umidade.roundToInt()}%"
}

private fun iconeDoStatus(status: AmbienteStatus): String = when (status) {
    AmbienteStatus.CONFORTAVEL -> "🌿"
    AmbienteStatus.QUENTE_SECO -> "🔥"
    AmbienteStatus.QUENTE_ABAFADO -> "🥵"
    AmbienteStatus.FRIO_SECO -> "❄️"
    AmbienteStatus.FRIO_UMIDO -> "🌧️"
    AmbienteStatus.ATENCAO -> "⚠️"
}

private fun corDoStatus(status: AmbienteStatus): Color = when (status) {
    AmbienteStatus.CONFORTAVEL -> Color(0xFF2E7D32)
    AmbienteStatus.QUENTE_SECO -> Color(0xFFEF6C00)
    AmbienteStatus.QUENTE_ABAFADO -> Color(0xFFC62828)
    AmbienteStatus.FRIO_SECO -> Color(0xFF1565C0)
    AmbienteStatus.FRIO_UMIDO -> Color(0xFF0D47A1)
    AmbienteStatus.ATENCAO -> Color(0xFFF9A825)
}

/** Cor do texto/conteúdo para garantir bom contraste sobre a cor de fundo. */
private fun corDoConteudo(status: AmbienteStatus): Color = when (status) {
    AmbienteStatus.ATENCAO -> Color(0xFF212121)
    else -> Color.White
}
