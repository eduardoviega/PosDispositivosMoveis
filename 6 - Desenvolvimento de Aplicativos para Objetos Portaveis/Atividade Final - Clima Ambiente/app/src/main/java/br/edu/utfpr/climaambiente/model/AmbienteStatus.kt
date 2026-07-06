package br.edu.utfpr.climaambiente.model

/**
 * Classificação de conforto do ambiente calculada a partir da temperatura e da umidade.
 * O mapeamento de cada status para cor e ícone é responsabilidade da camada de UI.
 */
enum class AmbienteStatus {
    /** Temperatura entre 20°C e 26°C e umidade entre 40% e 60%. */
    CONFORTAVEL,

    /** Temperatura acima de 30°C e umidade abaixo de 35%. */
    QUENTE_SECO,

    /** Temperatura acima de 30°C e umidade acima de 70%. */
    QUENTE_ABAFADO,

    /** Temperatura abaixo de 18°C e umidade abaixo de 35%. */
    FRIO_SECO,

    /** Temperatura abaixo de 18°C e umidade acima de 70%. */
    FRIO_UMIDO,

    /** Qualquer outra combinação (condições intermediárias). */
    ATENCAO
}
