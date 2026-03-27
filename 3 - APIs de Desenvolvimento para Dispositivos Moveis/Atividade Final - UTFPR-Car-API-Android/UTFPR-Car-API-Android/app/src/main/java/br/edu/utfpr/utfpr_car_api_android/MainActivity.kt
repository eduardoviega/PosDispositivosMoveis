package br.edu.utfpr.utfpr_car_api_android

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.edu.utfpr.utfpr_car_api_android.model.CarModel
import br.edu.utfpr.utfpr_car_api_android.service.ApiResult
import br.edu.utfpr.utfpr_car_api_android.service.AuthHelper
import br.edu.utfpr.utfpr_car_api_android.service.RetrofitClient
import br.edu.utfpr.utfpr_car_api_android.service.safeApiCall
import br.edu.utfpr.utfpr_car_api_android.ui.loadUrl
import br.edu.utfpr.utfpr_car_api_android.ui.theme.UTFPRCarAPIAndroidTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private var cars by mutableStateOf<List<CarModel>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var isError by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocationHelper(this).checkLocationPermissionAndRequest()
        enableEdgeToEdge()
        setContent {
            UTFPRCarAPIAndroidTheme {
                MainScreenContent()
            }
        }
    }

    @Composable
    private fun MainScreenContent() {
        MainScreen(
            cars = cars,
            isLoading = isLoading,
            isError = isError,
            onRefresh = { fetchItems() },
            onLogout = { onLogout() }
        )
    }

    override fun onResume() {
        super.onResume()
        fetchItems()
    }

    private fun fetchItems() {
        isLoading = true
        isError = false
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getCars() }

            withContext(Dispatchers.Main) {
                isLoading = false
                when (result) {
                    is ApiResult.Success -> {
                        cars = result.data
                        isError = false
                    }

                    is ApiResult.Error -> {
                        isError = true
                    }
                }
            }
        }
    }

    private fun onLogout() {
        AuthHelper.signOut(this) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    cars: List<CarModel>,
    isLoading: Boolean,
    isError: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(topBar = {
        MainTopBar(onRefresh = onRefresh, onLogout = onLogout)
    }, floatingActionButton = {
        MainFloatingActionButton(
            onClick = {
                context.startActivity(Intent(context, NewCarActivity::class.java))
            })
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                isError -> {
                    ErrorSection(onRetry = onRefresh, modifier = Modifier.align(Alignment.Center))
                }

                cars.isEmpty() -> {
                    Text(
                        stringResource(R.string.nenhum_carro_encontrado),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    CarList(
                        cars = cars, onItemClick = { carId ->
                            context.startActivity(CarDetailActivity.newIntent(context, carId))
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(onRefresh: () -> Unit, onLogout: () -> Unit) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.lista_de_carros))
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Default.Refresh, contentDescription = stringResource(R.string.atualizar)
                )
            }
            IconButton(onClick = onLogout) {
                Icon(
                    Icons.AutoMirrored.Rounded.ExitToApp,
                    contentDescription = stringResource(R.string.sair)
                )
            }
        })
}

@Composable
private fun MainFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.adicionar_carro))
    }
}

@Composable
private fun ErrorSection(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.erro_ao_buscar_carros))
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
            Text(stringResource(R.string.tentar_novamente))
        }
    }
}

@Composable
fun CarList(cars: List<CarModel>, onItemClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(cars) { car ->
            CarItem(
                car = car, onItemClick = { onItemClick(car.id) })
            HorizontalDivider()
        }
    }
}

@Composable
fun CarItem(car: CarModel, onItemClick: () -> Unit) {
    ListItem(
        leadingContent = { CarImage(imageUrl = car.imageUrl) },
        headlineContent = { Text(car.name) },
        supportingContent = {
            Column {
                Text(stringResource(R.string.ano_value, car.year))
                Text(stringResource(R.string.placa_value, car.licence))
            }
        },
        modifier = Modifier.clickable { onItemClick() })
}


@Composable
fun CarImage(imageUrl: String, size: Int = 64) {
    AndroidView(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape), factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }, update = { imageView -> imageView.loadUrl(imageUrl) })
}
