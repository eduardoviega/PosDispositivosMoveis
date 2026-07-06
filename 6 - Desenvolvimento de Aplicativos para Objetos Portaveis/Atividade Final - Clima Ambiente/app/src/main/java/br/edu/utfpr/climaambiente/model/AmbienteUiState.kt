package br.edu.utfpr.climaambiente.model

/**
 * Estado imutável da interface padrão MVVM.
 *
 * @property temperatura Última temperatura lida, em graus Celsius.
 * @property umidade Última umidade relativa lida, em porcentagem.
 * @property status Classificação de conforto atual do ambiente.
 * @property tituloStatus Texto curto do status, exibido ao usuário (em português).
 * @property descricaoStatus Mensagem descritiva do status, exibida ao usuário (em português).
 * @property temperaturaDisponivel `false` quando o dispositivo não possui sensor de temperatura.
 * @property umidadeDisponivel `false` quando o dispositivo não possui sensor de umidade.
 * @property possuiLeituraTemperatura `true` após a primeira leitura válida de temperatura.
 * @property possuiLeituraUmidade `true` após a primeira leitura válida de umidade.
 */
data class AmbienteUiState(
    val temperatura: Float = 0f,
    val umidade: Float = 0f,
    val status: AmbienteStatus = AmbienteStatus.ATENCAO,
    val tituloStatus: String = "",
    val descricaoStatus: String = "",
    val temperaturaDisponivel: Boolean = true,
    val umidadeDisponivel: Boolean = true,
    val possuiLeituraTemperatura: Boolean = false,
    val possuiLeituraUmidade: Boolean = false
)
