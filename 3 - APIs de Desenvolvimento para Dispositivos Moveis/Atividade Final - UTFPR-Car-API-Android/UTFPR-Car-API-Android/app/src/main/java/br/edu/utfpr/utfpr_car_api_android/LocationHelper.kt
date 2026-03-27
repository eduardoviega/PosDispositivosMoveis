package br.edu.utfpr.utfpr_car_api_android

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(private val activity: ComponentActivity) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private val locationPermissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLastLocation()
            } else {
                val text = activity.getString(R.string.permissao_de_localizacao_negada)
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
            }
        }

    fun checkLocationPermissionAndRequest() {
        val fineLocation = ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION)

        when {
            fineLocation == PERMISSION_GRANTED && coarseLocation == PERMISSION_GRANTED -> {
                getLastLocation()
            }

            activity.shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }

            activity.shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) -> {
                locationPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            }

            else -> {
                locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getLastLocation() {
        val fineLocation = ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION)

        if (fineLocation != PERMISSION_GRANTED && coarseLocation != PERMISSION_GRANTED) {
            checkLocationPermissionAndRequest()
            return
        }

        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if (!task.isSuccessful || task.result == null) {
                val text = activity.getString(R.string.nao_foi_possivel_obter_localizacao)
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
