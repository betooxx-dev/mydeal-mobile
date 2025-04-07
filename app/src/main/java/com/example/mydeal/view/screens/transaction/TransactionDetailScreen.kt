package com.example.mydeal.view.screens.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.api.RetrofitClient
import com.example.mydeal.ui.theme.LightGreen
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.navigation.Screen
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// Modelo para la respuesta de una transacción individual
data class TransactionDetailResponse(
    val message: String,
    val data: TransactionDetail
)

// Modelo para los detalles de una transacción
data class TransactionDetail(
    val id: String,
    val amount: String,
    val isExpense: Boolean,
    val description: String,
    val category: String,
    val date: String,
    val time: String = "",
    val isRecurring: Boolean,
    val recurringPeriod: String,
    val receiptUrl: String?,
    val location: String?
)

// Interface para el servicio de API
interface TransactionDetailApi {
    @GET("transactions/{id}")
    suspend fun getTransactionById(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String
    ): Response<TransactionDetailResponse>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String
    ): Response<Map<String, String>>
}

// ViewModel para la pantalla de detalles
class TransactionDetailViewModel : ViewModel() {
    private val _transactionDetail = MutableLiveData<ApiResponse<TransactionDetail>>()
    val transactionDetail: LiveData<ApiResponse<TransactionDetail>> = _transactionDetail

    val _deleteResult = MutableLiveData<ApiResponse<String>>()
    val deleteResult: LiveData<ApiResponse<String>> = _deleteResult

    fun getTransactionById(context: android.content.Context, transactionId: String) {
        _transactionDetail.value = ApiResponse.Loading

        viewModelScope.launch {
            try {
                val authService = com.example.mydeal.model.service.AuthService(context)
                if (!authService.isAuthenticated()) {
                    _transactionDetail.value = ApiResponse.Error("No autenticado")
                    return@launch
                }

                val token = authService.getAuthToken()
                val api = RetrofitClient.getAuthInstance(context).create(TransactionDetailApi::class.java)
                val response = api.getTransactionById(token, transactionId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _transactionDetail.value = ApiResponse.Success(body.data)
                    } else {
                        _transactionDetail.value = ApiResponse.Error("Respuesta vacía")
                    }
                } else {
                    _transactionDetail.value = ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _transactionDetail.value = ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun deleteTransaction(context: android.content.Context, transactionId: String) {
        _deleteResult.value = ApiResponse.Loading

        viewModelScope.launch {
            try {
                val authService = com.example.mydeal.model.service.AuthService(context)
                if (!authService.isAuthenticated()) {
                    _deleteResult.value = ApiResponse.Error("No autenticado")
                    return@launch
                }

                val token = authService.getAuthToken()
                val api = RetrofitClient.getAuthInstance(context).create(TransactionDetailApi::class.java)
                val response = api.deleteTransaction(token, transactionId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _deleteResult.value = ApiResponse.Success(body["message"] ?: "Transacción eliminada")
                    } else {
                        _deleteResult.value = ApiResponse.Success("Transacción eliminada")
                    }
                } else {
                    _deleteResult.value = ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _deleteResult.value = ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    navController: NavController,
    viewModel: TransactionDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val transactionDetailResult by viewModel.transactionDetail.observeAsState()
    val deleteResult by viewModel.deleteResult.observeAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "MX"))

    // Cargar datos al iniciar la pantalla
    LaunchedEffect(transactionId) {
        viewModel.getTransactionById(context, transactionId)
    }

    // Navegar de vuelta a la lista cuando se elimina con éxito
    LaunchedEffect(deleteResult) {
        if (deleteResult is ApiResponse.Success) {
            navController.navigate(Screen.TransactionList.route) {
                popUpTo(Screen.TransactionList.route) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de Transacción",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (transactionDetailResult) {
                is ApiResponse.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = LightGreen
                    )
                }
                is ApiResponse.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Error al cargar los datos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = (transactionDetailResult as ApiResponse.Error).message,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.getTransactionById(context, transactionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
                is ApiResponse.Success -> {
                    val transaction = (transactionDetailResult as ApiResponse.Success<TransactionDetail>).data
                    val amount = transaction.amount.toDoubleOrNull() ?: 0.0
                    val formattedDate = try {
                        val date = dateFormatter.parse(transaction.date)
                        if (date != null) displayFormatter.format(date) else transaction.date
                    } catch (e: Exception) {
                        transaction.date
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Encabezado con monto
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (transaction.isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (transaction.isExpense) "Gasto" else "Ingreso",
                                    color = if (transaction.isExpense) Color(0xFFE53935) else Color(0xFF43A047),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = currencyFormatter.format(amount),
                                    color = if (transaction.isExpense) Color(0xFFE53935) else Color(0xFF43A047),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (transaction.isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                                    )
                                ) {
                                    Text(
                                        text = transaction.category,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        fontSize = 14.sp
                                    )
                                }

                                if (transaction.isRecurring) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = LightGreen
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Repeat,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )

                                            Spacer(modifier = Modifier.width(4.dp))

                                            Text(
                                                text = when(transaction.recurringPeriod.lowercase()) {
                                                    "daily" -> "Diario"
                                                    "weekly" -> "Semanal"
                                                    "monthly" -> "Mensual"
                                                    "yearly" -> "Anual"
                                                    else -> "Recurrente"
                                                },
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Sección de detalles
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Detalles de la Transacción",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Descripción
                                DetailItem(
                                    icon = Icons.Default.Description,
                                    title = "Descripción",
                                    value = transaction.description,
                                    iconTint = LightGreen
                                )

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                // Fecha
                                DetailItem(
                                    icon = Icons.Default.DateRange,
                                    title = "Fecha",
                                    value = formattedDate,
                                    iconTint = LightGreen
                                )

                                // Solo mostrar ubicación si existe
                                if (!transaction.location.isNullOrBlank()) {
                                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                                    // Ubicación
                                    DetailItem(
                                        icon = Icons.Default.LocationOn,
                                        title = "Ubicación",
                                        value = transaction.location,
                                        iconTint = LightGreen
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                // ID de transacción
                                DetailItem(
                                    icon = Icons.Default.Info,
                                    title = "ID de Transacción",
                                    value = transaction.id,
                                    iconTint = LightGreen
                                )
                            }
                        }

                        // Comprobante (si existe)
                        if (!transaction.receiptUrl.isNullOrBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Comprobante",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    // Imagen del comprobante
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.LightGray)
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(transaction.receiptUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Comprobante",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            navController.navigate(Screen.ReceiptCapture.createRoute(transaction.id))
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LightGreen
                                        ),
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Cambiar Comprobante")
                                    }
                                }
                            }
                        } else {
                            // Opción para agregar comprobante
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE3F2FD)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = LightGreen,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Text(
                                            text = "Añadir Comprobante",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp
                                        )

                                        Text(
                                            text = "Toma una foto de tu recibo o factura",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            navController.navigate(Screen.ReceiptCapture.createRoute(transaction.id))
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LightGreen
                                        )
                                    ) {
                                        Text("Capturar")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botones de acción
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomButton(
                                text = "Volver a lista",
                                onClick = {
                                    navController.navigate(Screen.TransactionList.route) {
                                        popUpTo(Screen.TransactionList.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                isSecondary = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                null -> {
                    // Estado inicial, mostrar carga
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = LightGreen
                    )
                }
            }

            // Mostrar indicador de carga durante la eliminación
            if (deleteResult is ApiResponse.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // Diálogo de error en la eliminación
            if (deleteResult is ApiResponse.Error) {
                AlertDialog(
                    onDismissRequest = { /* No hacer nada */ },
                    title = { Text("Error") },
                    text = { Text((deleteResult as ApiResponse.Error).message) },
                    confirmButton = {
                        Button(
                            onClick = { viewModel._deleteResult.value = null },
                            colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }

        // Diálogo de confirmación para eliminar
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Eliminar Transacción") },
                text = { Text("¿Estás seguro de que quieres eliminar esta transacción? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTransaction(context, transactionId)
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935)
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    iconTint: Color = Color(0xFF1976D2)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                text = value,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}