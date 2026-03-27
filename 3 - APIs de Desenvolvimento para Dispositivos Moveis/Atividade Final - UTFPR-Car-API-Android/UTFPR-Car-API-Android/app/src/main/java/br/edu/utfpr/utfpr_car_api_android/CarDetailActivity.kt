package br.edu.utfpr.utfpr_car_api_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.edu.utfpr.utfpr_car_api_android.model.CarModel
import br.edu.utfpr.utfpr_car_api_android.model.PlaceModel
import br.edu.utfpr.utfpr_car_api_android.service.ApiResult
import br.edu.utfpr.utfpr_car_api_android.service.RetrofitClient
import br.edu.utfpr.utfpr_car_api_android.service.safeApiCall
import br.edu.utfpr.utfpr_car_api_android.ui.MapComponent
import br.edu.utfpr.utfpr_car_api_android.ui.loadUrl
import br.edu.utfpr.utfpr_car_api_android.ui.theme.UTFPRCarAPIAndroidTheme
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarDetailActivity : ComponentActivity() {
    private var carModel by mutableStateOf<CarModel?>(null)
    private var isProcessing by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UTFPRCarAPIAndroidTheme {
                CarDetailContent()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadItem()
    }

    @Composable
    private fun CarDetailContent() {
        Box(modifier = Modifier.fillMaxSize()) {
            if (carModel != null) {
                CarDetailScreen(
                    carModel = carModel!!,
                    isProcessing = isProcessing,
                    onBack = { finish() },
                    onSave = { updatedCar -> saveItem(updatedCar) },
                    onDelete = { deleteItem() })
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (isProcessing) {
                LoadingOverlay()
            }
        }
    }

    @Composable
    private fun LoadingOverlay() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    private fun loadItem() {
        val itemId = intent.getStringExtra(ARG_ID) ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getCar(itemId) }

            withContext(Dispatchers.Main) {
                when (result) {
                    is ApiResult.Success -> {
                        carModel = result.data.value
                    }

                    is ApiResult.Error -> {
                        showErrorAndFinish(
                            getString(
                                R.string.erro_ao_carregar_detalhes, result.message
                            )
                        )
                    }
                }
            }
        }
    }

    private fun saveItem(updatedCar: CarModel) {
        isProcessing = true
        CoroutineScope(Dispatchers.IO).launch {
            val result =
                safeApiCall { RetrofitClient.apiService.updateCar(updatedCar.id, updatedCar) }

            withContext(Dispatchers.Main) {
                isProcessing = false
                when (result) {
                    is ApiResult.Success -> {
                        showToastAndFinish(getString(R.string.carro_atualizado))
                    }

                    is ApiResult.Error -> {
                        showToast(getString(R.string.erro_ao_atualizar, result.message))
                    }
                }
            }
        }
    }

    private fun deleteItem() {
        val itemId = carModel?.id ?: return
        isProcessing = true
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.deleteCar(itemId) }

            withContext(Dispatchers.Main) {
                isProcessing = false
                when (result) {
                    is ApiResult.Success -> {
                        showToastAndFinish(getString(R.string.carro_removido))
                    }

                    is ApiResult.Error -> {
                        showToast(getString(R.string.erro_ao_remover, result.message))
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToastAndFinish(message: String) {
        showToast(message)
        finish()
    }

    private fun showErrorAndFinish(message: String) {
        showToast(message)
        finish()
    }

    companion object {
        private const val ARG_ID = "arg_id"

        fun newIntent(context: Context, itemId: String): Intent {
            return Intent(context, CarDetailActivity::class.java).apply {
                putExtra(ARG_ID, itemId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    carModel: CarModel,
    isProcessing: Boolean,
    onBack: () -> Unit,
    onSave: (CarModel) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(carModel.name) }
    var year by remember { mutableStateOf(carModel.year) }
    var licence by remember { mutableStateOf(carModel.licence) }
    var location by remember {
        mutableStateOf(LatLng(carModel.place?.lat ?: 0.0, carModel.place?.long ?: 0.0))
    }

    Scaffold(
        topBar = { CarDetailTopBar(onBack, isProcessing) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CarHeaderImage(carModel.imageUrl)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.nome)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            )

            Spacer(modifier = Modifier.height(16.dp))

            CarInfoSection(carModel)

            Spacer(modifier = Modifier.height(16.dp))

            MapComponent(
                location = location,
                onLocationChange = { location = it },
                isProcessing = isProcessing
            )

            Spacer(modifier = Modifier.height(32.dp))

            ActionButtonsRow(
                isProcessing = isProcessing, onDelete = onDelete, onSave = {
                    onSave(
                        carModel.copy(
                            name = name,
                            year = year,
                            licence = licence,
                            place = PlaceModel(location.latitude, location.longitude)
                        )
                    )
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarDetailTopBar(onBack: () -> Unit, isProcessing: Boolean) {
    TopAppBar(
        title = { Text(stringResource(R.string.detalhes_do_carro)) }, navigationIcon = {
        IconButton(onClick = onBack, enabled = !isProcessing) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.voltar)
            )
        }
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    )
    )
}

@Composable
private fun CarHeaderImage(imageUrl: String?) {
    Box(
        modifier = Modifier.padding(vertical = 16.dp), contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            },
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            update = { imageView ->
                try {
                    imageView.loadUrl(imageUrl ?: "")
                } catch (_: Exception) {
                }
            })
    }
}

@Composable
private fun CarInfoSection(carModel: CarModel) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f)) {
            InfoRow(label = stringResource(R.string.ano_title), value = carModel.year)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            InfoRow(label = stringResource(R.string.placa_title), value = carModel.licence)
        }
    }
}

@Composable
private fun ActionButtonsRow(
    isProcessing: Boolean, onDelete: () -> Unit, onSave: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            enabled = !isProcessing,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.deletar))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onSave, modifier = Modifier.weight(1f), enabled = !isProcessing
        ) {
            Icon(Icons.Default.Done, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.salvar))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}