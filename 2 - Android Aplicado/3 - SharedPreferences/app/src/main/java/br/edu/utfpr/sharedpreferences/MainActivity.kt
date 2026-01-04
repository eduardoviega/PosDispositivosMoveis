package br.edu.utfpr.sharedpreferences

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit

private const val SP_NAME = "br.edu.utfpr.sharedpreferences"

class MainActivity : AppCompatActivity() {

    private lateinit var ivStar: ImageView
    var ligado = false

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivStar = findViewById(R.id.ivStar)
        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE)

        ligado = sharedPreferences.getBoolean("ligado", false)
        ivStar.setImageResource(
            if (ligado) android.R.drawable.star_big_on
            else android.R.drawable.star_big_off
        )
    }

    fun btOnOffOnClick(view: View) {
        ligado = !ligado
        ivStar.setImageResource(
            if (ligado) android.R.drawable.star_big_on
            else android.R.drawable.star_big_off
        )
        sharedPreferences.edit { putBoolean("ligado", ligado) }
    }

    fun btConfiguracoesOnClick(view: View) {
        val intent = android.content.Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}