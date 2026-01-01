package br.edu.utfpr.calculaimc.util

import kotlin.math.pow

fun calcularImc(peso: Double, altura: Double, locale:String): Double {
    if ( locale.equals( "en", ignoreCase = true ) ) {
        return 703 * ( peso / altura.pow(2.0))
    }
    return  ( peso / altura.pow(2.0))
}