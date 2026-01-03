package br.edu.utfpr.fuelcalc

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FuelListActivity : AppCompatActivity() {
    private lateinit var lvFuels: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fuel_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lvFuels = findViewById(R.id.lvFuels)

        lvFuels.setOnItemClickListener { parent, view, position, id ->
            val fuelName = parent.getItemAtPosition(position).toString()
            intent.putExtra("fuel", fuelName)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}