package br.edu.utfpr.trocatela

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.URI

class ConfirmarActivity : AppCompatActivity() {

    private lateinit var tvCod: TextView
    private lateinit var tvQtd: TextView
    private lateinit var tvValor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvCod = findViewById(R.id.tvCod)
        tvQtd = findViewById(R.id.tvQtd)
        tvValor = findViewById(R.id.tvValor)

        // Código recuperado como inteiro, pois foi transferido como inteiro
        val cod = intent.getIntExtra("cod", 0)
        val qtd = intent.getStringExtra("qtd")
        val valor = intent.getStringExtra("valor")

        tvCod.text = cod.toString()
        tvQtd.text = qtd
        tvValor.text = valor
    }

    fun btConfirmarComSMSOnClick(view: View) {
        val sms = "Cod: ${tvCod.text} Qtd: ${tvQtd.text} Valor: ${tvValor.text}"

        val intent = Intent(Intent.ACTION_VIEW)

        intent.setData(Uri.parse("sms:+55469121234"))
        intent.putExtra("sms_body", sms)

        startActivity(intent)
    }
}