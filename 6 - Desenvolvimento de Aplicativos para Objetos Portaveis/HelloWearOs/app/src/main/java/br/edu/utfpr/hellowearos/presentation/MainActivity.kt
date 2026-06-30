/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package br.edu.utfpr.hellowearos.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import br.edu.utfpr.hellowearos.R
import kotlinx.coroutines.delay
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        setContent {
            WearApp()
            WearReminderWater(this)
        }
    }
}

fun obterSaudacao(horaAtual: LocalTime): String {
    return when (horaAtual.hour) {
        in 0..11 -> "Bom dia flor do dia"
        in 12..17 -> "Boa Tarde campeão"
        else -> "Boa noite"
    }

}

@Composable
fun WearApp() {
    var hora by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            hora = LocalTime.now();
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = obterSaudacao(hora),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "agora são",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(5.dp))

            val display = String.format("%02d:%02d:%02d", hora.hour, hora.minute, hora.second);

            Text(
                text = display,
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            )
        }
    }
}

@Composable
fun WearReminderWater(context: Context) {
    var time by remember { mutableStateOf(LocalTime.now()) }
    var notified by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val channelId = "water_reminder_channel"
        val channelName = "Water Reminder"

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (manager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        while (true) {
            time = LocalTime.now()

            if (time.minute == 0 && !notified) {
                val notification = NotificationCompat.Builder(context, channelId)
                    .setContentTitle("💧 Hora de tomar Agua !")
                    .setContentText(" Vá beber agua agora")
                    .setSmallIcon(R.drawable.splash_icon)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .build();

                val notification2 = NotificationCompat.Builder(context, channelId)
                    .setContentTitle(" Hora de tomar Cafe !")
                    .setContentText(" Vá beber agua agora")
                    .setSmallIcon(R.drawable.splash_icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

                manager.notify(1, notification)
                manager.notify(1, notification2)
                notified = true;

            }
            if (time.minute != 56) {
                notified = false;
            }

            delay(1000L);
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}