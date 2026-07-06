package br.edu.utfpr.climaambiente.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Encapsula o acesso ao [SensorManager] do Android, isolando a camada de sensores do
 * restante do aplicativo (Repository/Data source dos sensores no padrão MVVM).
 *
 * A classe implementa [SensorEventListener] e converte os eventos brutos do sistema em
 * callbacks simples ([onTemperaturaChanged] / [onUmidadeChanged]) que a ViewModel consome
 * para atualizar o estado da interface.
 *
 * @param context contexto usado para obter o serviço de sensores do sistema.
 */
class AmbienteSensorManager(context: Context) : SensorEventListener {

    /** Serviço do Android responsável por fornecer acesso aos sensores do dispositivo. */
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    /** Sensor de temperatura ambiente; `null` quando o dispositivo não possui esse hardware. */
    private val temperaturaSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    /** Sensor de umidade relativa; `null` quando o dispositivo não possui esse hardware. */
    private val umidadeSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

    /** Indica se o sensor de temperatura está disponível neste dispositivo. */
    val temperaturaDisponivel: Boolean get() = temperaturaSensor != null

    /** Indica se o sensor de umidade está disponível neste dispositivo. */
    val umidadeDisponivel: Boolean get() = umidadeSensor != null

    /** Callback disparado a cada nova leitura de temperatura (°C). Definido pela ViewModel. */
    var onTemperaturaChanged: ((Float) -> Unit)? = null

    /** Callback disparado a cada nova leitura de umidade (%). Definido pela ViewModel. */
    var onUmidadeChanged: ((Float) -> Unit)? = null

    /** Registra os listeners nos sensores disponíveis. */
    fun startListening() {
        temperaturaSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        umidadeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /** Remove todos os listeners registrados por esta instância. */
    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    /** Recebe cada evento de sensor emitido pelo sistema e o encaminha. */
    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> onTemperaturaChanged?.invoke(event.values[0])
            Sensor.TYPE_RELATIVE_HUMIDITY -> onUmidadeChanged?.invoke(event.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
