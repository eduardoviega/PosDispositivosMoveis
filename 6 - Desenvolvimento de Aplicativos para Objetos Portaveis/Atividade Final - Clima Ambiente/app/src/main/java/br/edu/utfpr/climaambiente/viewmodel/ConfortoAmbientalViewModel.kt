package br.edu.utfpr.climaambiente.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.edu.utfpr.climaambiente.model.AmbienteStatus
import br.edu.utfpr.climaambiente.model.AmbienteUiState
import br.edu.utfpr.climaambiente.sensor.AmbienteSensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel do padrão MVVM.
 *
 * Concentra toda a regra de negócio do aplicativo: recebe as leituras cruas dos sensores
 * (via [AmbienteSensorManager]), classifica o conforto do ambiente e publica
 * um único [AmbienteUiState] imutável através de um [StateFlow].
 *
 * A tela apenas observa esse estado e não contém nenhuma lógica de decisão.
 *
 * Usa [AndroidViewModel] porque o [AmbienteSensorManager] precisa de um [Application] context
 * para acessar o serviço de sensores. Isso permite criar a ViewModel com a factory padrão do
 * Compose (`viewModel()`), sem factory customizada.
 */
class ConfortoAmbientalViewModel(application: Application) : AndroidViewModel(application) {
    private val sensorManager = AmbienteSensorManager(application)

    private val _uiState = MutableStateFlow(
        AmbienteUiState(
            temperaturaDisponivel = sensorManager.temperaturaDisponivel,
            umidadeDisponivel = sensorManager.umidadeDisponivel
        )
    )

    val uiState: StateFlow<AmbienteUiState> = _uiState.asStateFlow()

    init {
        // Conecta os callbacks do sensor aos métodos de negócio desta ViewModel.
        sensorManager.onTemperaturaChanged = ::atualizarTemperatura
        sensorManager.onUmidadeChanged = ::atualizarUmidade
    }

    fun iniciarMonitoramento() {
        sensorManager.startListening()
    }

    fun pararMonitoramento() {
        sensorManager.stopListening()
    }

    /** Processa uma nova leitura de temperatura e recalcula o estado da interface. */
    private fun atualizarTemperatura(novaTemperatura: Float) {
        val estadoAtual = _uiState.value
        publicarEstado(
            temperatura = novaTemperatura,
            umidade = estadoAtual.umidade,
            possuiLeituraTemperatura = true,
            possuiLeituraUmidade = estadoAtual.possuiLeituraUmidade
        )
    }

    /** Processa uma nova leitura de umidade e recalcula o estado da interface. */
    private fun atualizarUmidade(novaUmidade: Float) {
        val estadoAtual = _uiState.value
        publicarEstado(
            temperatura = estadoAtual.temperatura,
            umidade = novaUmidade,
            possuiLeituraTemperatura = estadoAtual.possuiLeituraTemperatura,
            possuiLeituraUmidade = true
        )
    }

    /** Regras de conforto para classificar o ambiente pela temperatura e umidade atuais. */
    private fun calcularStatus(temperatura: Float, umidade: Float): AmbienteStatus = when {
        temperatura in 20f..26f && umidade in 40f..60f -> AmbienteStatus.CONFORTAVEL
        temperatura > 30f && umidade < 35f -> AmbienteStatus.QUENTE_SECO
        temperatura > 30f && umidade > 70f -> AmbienteStatus.QUENTE_ABAFADO
        temperatura < 18f && umidade < 35f -> AmbienteStatus.FRIO_SECO
        temperatura < 18f && umidade > 70f -> AmbienteStatus.FRIO_UMIDO
        else -> AmbienteStatus.ATENCAO
    }

    /** Título curto exibido ao usuário para cada status. */
    private fun tituloDe(status: AmbienteStatus): String = when (status) {
        AmbienteStatus.CONFORTAVEL -> "Confortável"
        AmbienteStatus.QUENTE_SECO -> "Quente e seco"
        AmbienteStatus.QUENTE_ABAFADO -> "Quente e abafado"
        AmbienteStatus.FRIO_SECO -> "Frio e seco"
        AmbienteStatus.FRIO_UMIDO -> "Frio e úmido"
        AmbienteStatus.ATENCAO -> "Atenção"
    }

    /** Mensagem descritiva exibida ao usuário para cada status. */
    private fun descricaoDe(status: AmbienteStatus): String = when (status) {
        AmbienteStatus.CONFORTAVEL -> "O ambiente está agradável."
        AmbienteStatus.QUENTE_SECO -> "Beba água e mantenha-se hidratado."
        AmbienteStatus.QUENTE_ABAFADO -> "Evite exposição prolongada ao calor."
        AmbienteStatus.FRIO_SECO -> "O ar pode causar ressecamento."
        AmbienteStatus.FRIO_UMIDO -> "Ambiente com sensação elevada de umidade."
        AmbienteStatus.ATENCAO -> "Condições intermediárias."
    }

    /** Monta e publica um novo [AmbienteUiState] completo. */
    private fun publicarEstado(
        temperatura: Float,
        umidade: Float,
        possuiLeituraTemperatura: Boolean,
        possuiLeituraUmidade: Boolean
    ) {
        val status = calcularStatus(temperatura, umidade)
        _uiState.update { estadoAtual ->
            estadoAtual.copy(
                temperatura = temperatura,
                umidade = umidade,
                status = status,
                tituloStatus = tituloDe(status),
                descricaoStatus = descricaoDe(status),
                possuiLeituraTemperatura = possuiLeituraTemperatura,
                possuiLeituraUmidade = possuiLeituraUmidade
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopListening()
    }
}
