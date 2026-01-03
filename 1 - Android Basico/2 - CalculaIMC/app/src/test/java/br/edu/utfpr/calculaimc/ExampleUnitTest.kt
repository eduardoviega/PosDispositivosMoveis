package br.edu.utfpr.calculaimc

import br.edu.utfpr.calculaimc.util.calcularImc
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun imc_is_correct() {
        assertTrue(
            "O valor do IMC está correto?",
            calcularImc(90.0, 1.90, "pt") in 24.93 .. 24.94
        )
    }
}