package br.edu.utfpr.fuelcalc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var tvFirstFuelValue: TextView
    private lateinit var tvSecondFuelValue: TextView
    private lateinit var tvResultValue: TextView
    private lateinit var etFirstFuelPrice: TextInputEditText
    private lateinit var etSecondFuelPrice: TextInputEditText
    private lateinit var etFirstFuelRange: TextInputEditText
    private lateinit var etSecondFuelRange: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvFirstFuelValue = findViewById(R.id.first_fuel_value)
        tvSecondFuelValue = findViewById(R.id.second_fuel_value)
        tvResultValue = findViewById(R.id.result_value)

        etFirstFuelPrice = findViewById(R.id.first_fuel_price_value)
        etSecondFuelPrice = findViewById(R.id.second_fuel_price_value)
        etFirstFuelRange = findViewById(R.id.first_fuel_range_value)
        etSecondFuelRange = findViewById(R.id.second_fuel_range_value)
    }

    fun btFirstFuelOnClick(view: View) {
        val intent = Intent(this, FuelListActivity::class.java)
        getFirstResult.launch(intent)
    }

    private val getFirstResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val fuelName = result.data?.getStringExtra("fuel")
            tvFirstFuelValue.text = fuelName.toString()
        }
    }

    fun btSecondFuelOnClick(view: View) {
        val intent = Intent(this, FuelListActivity::class.java)
        getSecondResult.launch(intent)
    }

    private val getSecondResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val fuelName = result.data?.getStringExtra("fuel")
            tvSecondFuelValue.text = fuelName.toString()
        }
    }

    fun btCalculateOnClick(view: View) {
        val firstPrice = etFirstFuelPrice.text.toString().toDoubleOrNull()
        val secondPrice = etSecondFuelPrice.text.toString().toDoubleOrNull()
        val firstRange = etFirstFuelRange.text.toString().toDoubleOrNull()
        val secondRange = etSecondFuelRange.text.toString().toDoubleOrNull()

        val none = getString(R.string.none)
        val fuelsNotSelected = tvFirstFuelValue.text == none || tvSecondFuelValue.text == none
        val fieldsNotFilled =
            firstPrice == null || secondPrice == null || firstRange == null || secondRange == null

        if (fuelsNotSelected || fieldsNotFilled) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val firstFuelCost = firstPrice / firstRange
        val secondFuelCost = secondPrice / secondRange

        var betterFuel: String? = null
        if (firstFuelCost < secondFuelCost) {
            betterFuel = tvFirstFuelValue.text.toString()
        } else if (secondFuelCost < firstFuelCost) {
            betterFuel = tvSecondFuelValue.text.toString()
        }

        if (betterFuel != null) {
            tvResultValue.text = getString(R.string.is_better, betterFuel)
        } else {
            tvResultValue.text = getString(R.string.same_value)
        }
    }
}