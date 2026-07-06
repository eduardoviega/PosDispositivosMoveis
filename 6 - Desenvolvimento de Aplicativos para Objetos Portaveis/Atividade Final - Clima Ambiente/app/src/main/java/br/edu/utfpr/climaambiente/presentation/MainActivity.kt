/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package br.edu.utfpr.climaambiente.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import br.edu.utfpr.climaambiente.presentation.theme.ClimaAmbienteTheme
import br.edu.utfpr.climaambiente.ui.screen.ConfortoAmbientalScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClimaAmbienteApp()
        }
    }
}

@Composable
fun ClimaAmbienteApp() {
    ClimaAmbienteTheme {
        AppScaffold {
            ConfortoAmbientalScreen()
        }
    }
}
