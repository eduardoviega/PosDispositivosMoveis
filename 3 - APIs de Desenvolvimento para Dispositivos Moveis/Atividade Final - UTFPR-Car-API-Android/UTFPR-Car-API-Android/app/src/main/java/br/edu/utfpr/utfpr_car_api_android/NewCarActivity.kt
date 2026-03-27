package br.edu.utfpr.utfpr_car_api_android

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import br.edu.utfpr.utfpr_car_api_android.model.CarModel
import br.edu.utfpr.utfpr_car_api_android.model.PlaceModel
import br.edu.utfpr.utfpr_car_api_android.service.ApiResult
import br.edu.utfpr.utfpr_car_api_android.service.RetrofitClient
import br.edu.utfpr.utfpr_car_api_android.service.safeApiCall
import br.edu.utfpr.utfpr_car_api_android.ui.MapComponent
import br.edu.utfpr.utfpr_car_api_android.ui.loadUrl
import br.edu.utfpr.utfpr_car_api_android.ui.theme.UTFPRCarAPIAndroidTheme
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

class NewCarActivity : ComponentActivity() {
    private var isProcessing by mutableStateOf(false)
    private var imageUrl by mutableStateOf("")
    private lateinit var imageUri: Uri
    private var imageFile: File? = null

    private val cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            imageUrl = imageUri.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UTFPRCarAPIAndroidTheme {
                NewCarContent()
            }
        }
    }

    @Composable
    private fun NewCarContent() {
        Box(modifier = Modifier.fillMaxSize()) {
            NewCarScreen(
                isProcessing = isProcessing,
                imageUrl = imageUrl,
                onBack = { finish() },
                takePicture = { takePicture() },
                onSave = { name, year, licence, location ->
                    saveCar(name, year, licence, location)
                })

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

    private fun takePicture() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                1001
            )
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = createImageUri()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraLauncher.launch(intent)
    }

    private fun createImageUri(): Uri {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        return FileProvider.getUriForFile(
            this,
            "br.edu.utfpr.utfpr_car_api_android.fileprovider",
            imageFile!!
        )
    }

    private fun saveCar(name: String, year: String, licence: String, location: LatLng) {
        if (name.isBlank() || year.isBlank() || licence.isBlank()) {
            showToast(getString(R.string.preencha_campos_obrigatorios))
            return
        }

        if (imageFile != null) {
            uploadImageToFirebase(name, year, licence, location)
        } else {
            showToast(getString(R.string.foto_obrigatoria))
        }
    }

    private fun uploadImageToFirebase(
        name: String,
        year: String,
        licence: String,
        location: LatLng
    ) {
        imageFile?.let { file ->
            isProcessing = true
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            val byteArrayOutputStream = ByteArrayOutputStream()
            val imageBitmap = BitmapFactory.decodeFile(file.path)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()

            imageRef.putBytes(data)
                .addOnFailureListener {
                    isProcessing = false
                    showToast(getString(R.string.falha_upload_imagem))
                }
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        imageUrl = downloadUrl
                        saveData(name, year, licence, downloadUrl, location)
                    }
                }
        }
    }

    private fun saveData(
        name: String,
        year: String,
        licence: String,
        photoUrl: String,
        location: LatLng
    ) {
        isProcessing = true
        CoroutineScope(Dispatchers.IO).launch {
            val newCar = CarModel(
                id = abs(SecureRandom().nextInt()).toString(),
                name = name,
                year = year,
                licence = licence,
                imageUrl = photoUrl,
                place = PlaceModel(location.latitude, location.longitude)
            )

            val result = safeApiCall { RetrofitClient.apiService.addCar(newCar) }

            withContext(Dispatchers.Main) {
                isProcessing = false
                when (result) {
                    is ApiResult.Success -> {
                        showToast(getString(R.string.carro_cadastrado_com_sucesso))
                        finish()
                    }

                    is ApiResult.Error -> {
                        showToast(getString(R.string.erro_ao_cadastrar, result.message))
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCarScreen(
    isProcessing: Boolean,
    imageUrl: String,
    onBack: () -> Unit,
    takePicture: () -> Unit,
    onSave: (String, String, String, LatLng) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var licence by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(LatLng(-25.4284, -49.2733)) } // Curitiba

    Scaffold(
        topBar = { NewCarTopBar(onBack, isProcessing) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NewCarImageSection(
                imageUrl = imageUrl,
                takePicture = takePicture
            )

            Spacer(modifier = Modifier.height(16.dp))

            NewCarFormSection(
                name = name,
                onNameChange = { name = it },
                year = year,
                onYearChange = { year = it },
                licence = licence,
                onLicenceChange = { licence = it },
                isProcessing = isProcessing
            )

            Spacer(modifier = Modifier.height(24.dp))

            MapComponent(
                location = location,
                onLocationChange = { location = it },
                isProcessing = isProcessing
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSave(name, year, licence, location) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text(stringResource(R.string.cadastrar))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewCarTopBar(onBack: () -> Unit, isProcessing: Boolean) {
    TopAppBar(
        title = { Text(stringResource(R.string.novo_carro)) },
        navigationIcon = {
            IconButton(onClick = onBack, enabled = !isProcessing) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.voltar)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Composable
private fun NewCarImageSection(imageUrl: String, takePicture: () -> Unit) {
    Box(
        modifier = Modifier.padding(vertical = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isBlank()) {
                Text(
                    text = stringResource(R.string.sem_imagem),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    update = { imageView -> imageView.loadUrl(imageUrl) },
                    factory = { ctx ->
                        ImageView(ctx).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
                    })
            }
        }

        IconButton(
            onClick = takePicture,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun NewCarFormSection(
    name: String,
    onNameChange: (String) -> Unit,
    year: String,
    onYearChange: (String) -> Unit,
    licence: String,
    onLicenceChange: (String) -> Unit,
    isProcessing: Boolean
) {
    Column {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.nome_do_carro)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = year,
                onValueChange = onYearChange,
                label = { Text(stringResource(R.string.ano)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isProcessing
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = licence,
                onValueChange = onLicenceChange,
                label = { Text(stringResource(R.string.placa)) },
                modifier = Modifier.weight(1f),
                enabled = !isProcessing
            )
        }
    }
}
