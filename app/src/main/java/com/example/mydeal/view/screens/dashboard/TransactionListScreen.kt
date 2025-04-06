package com.example.mydeal.view.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.api.RetrofitClient
import com.example.mydeal.ui.theme.LightGreen
import com.example.mydeal.view.navigation.Screen
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// Modelos de datos
data class Transaction(
    val id: String,
    val amount: String,
    val isExpense: Boolean,
    val description: String,
    val category: String,
    val date: String,
    val isRecurring: Boolean,
    val recurringPeriod: String,
    val receiptUrl: String?,
    val location: String?
)

data class TransactionApiResponse(
    val message: String,
    val data: List<Transaction>
)

// Interface para el servicio de API
interface TransactionApi {
    @GET("transactions")
    suspend fun getTransactions(@Header("Authorization") token: String): Response<TransactionApiResponse>
}

// ViewModel para manejar las transacciones
class TransactionListViewModel : ViewModel() {
    private val _transactionsResult = MutableLiveData<ApiResponse<List<Transaction>>>()
    val transactionsResult: LiveData<ApiResponse<List<Transaction>>> = _transactionsResult

    fun fetchTransactions(context: android.content.Context) {
        _transactionsResult.value = ApiResponse.Loading

        viewModelScope.launch {
            try {
                val authService = com.example.mydeal.model.service.AuthService(context)
                if (!authService.isAuthenticated()) {
                    _transactionsResult.value = ApiResponse.Error("No autenticado")
                    return@launch
                }

                val token = authService.getAuthToken()
                val api = RetrofitClient.getAuthInstance(context).create(TransactionApi::class.java)
                val response = api.getTransactions(token)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _transactionsResult.value = ApiResponse.Success(body.data)
                    } else {
                        _transactionsResult.value = ApiResponse.Error("Respuesta vacía")
                    }
                } else {
                    _transactionsResult.value = ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _transactionsResult.value = ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: TransactionListViewModel = viewModel()
) {
    // Estados para la UI
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var showFilterOptions by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val transactionsResult by viewModel.transactionsResult.observeAsState()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormatter = SimpleDateFormat("dd MMM, yyyy", Locale("es", "MX"))

    // Cargar datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.fetchTransactions(context)
    }

    // Formato de moneda
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    // Obtener la lista de transacciones o una lista vacía si hay error o está cargando
    val transactions = when (transactionsResult) {
        is ApiResponse.Success -> (transactionsResult as ApiResponse.Success<List<Transaction>>).data
        else -> emptyList()
    }

    // Filtrar las transacciones
    val filteredTransactions = when (selectedFilter) {
        "Gastos" -> transactions.filter { it.isExpense }
        "Ingresos" -> transactions.filter { !it.isExpense }
        "Con comprobantes" -> transactions.filter { !it.receiptUrl.isNullOrEmpty() }
        else -> transactions
    }.filter {
        if (searchQuery.isEmpty()) true
        else it.description.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    // Formatear la fecha para agrupar y mostrar
    val formattedTransactions = filteredTransactions.map {
        val parsedDate = dateFormatter.parse(it.date)
        val formattedDate = if (parsedDate != null) displayFormatter.format(parsedDate) else it.date
        it to formattedDate
    }

    // Agrupar por fecha
    val groupedTransactions = formattedTransactions.groupBy { it.second }
        .mapValues { entry -> entry.value.map { it.first } }

    // Calcular totales
    val totalIncome = transactions.filter { !it.isExpense }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalExpense = transactions.filter { it.isExpense }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val balance = totalIncome - totalExpense

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearchVisible) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Buscar transacción...") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.White,
                                    cursorColor = Color.White
                                ),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = "Transacciones",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isSearchVisible) {
                                isSearchVisible = false
                                searchQuery = ""
                            } else {
                                navController.popBackStack()
                            }
                        }) {
                            Icon(
                                imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.ArrowBack,
                                contentDescription = if (isSearchVisible) "Cerrar búsqueda" else "Volver"
                            )
                        }
                    },
                    actions = {
                        // Botón de búsqueda
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        }

                        // Botón de filtro
                        IconButton(onClick = { showFilterOptions = !showFilterOptions }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtrar"
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

                // Opciones de filtro
                AnimatedVisibility(visible = showFilterOptions) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Filtrar por",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedFilter == "Todos",
                                    onClick = { selectedFilter = "Todos" },
                                    label = { Text("Todos") }
                                )

                                FilterChip(
                                    selected = selectedFilter == "Gastos",
                                    onClick = { selectedFilter = "Gastos" },
                                    label = { Text("Gastos") }
                                )

                                FilterChip(
                                    selected = selectedFilter == "Ingresos",
                                    onClick = { selectedFilter = "Ingresos" },
                                    label = { Text("Ingresos") }
                                )

                                FilterChip(
                                    selected = selectedFilter == "Con comprobantes",
                                    onClick = { selectedFilter = "Con comprobantes" },
                                    label = { Text("Con comprobantes") }
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) },
                containerColor = LightGreen
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Transacción",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Resumen de transacciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Resumen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Ingresos
                        Column {
                            Text(
                                text = "Ingresos",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Text(
                                text = currencyFormatter.format(totalIncome),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF43A047),
                                fontSize = 16.sp
                            )
                        }

                        // Gastos
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Gastos",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Text(
                                text = currencyFormatter.format(totalExpense),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935),
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Balance",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )

                        Text(
                            text = currencyFormatter.format(balance),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (balance >= 0) Color(0xFF43A047) else Color(0xFFE53935)
                        )
                    }
                }
            }

            // Estado de carga y errores
            when (transactionsResult) {
                is ApiResponse.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ApiResponse.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Error al cargar las transacciones",
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = (transactionsResult as ApiResponse.Error).message,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.fetchTransactions(context) }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                is ApiResponse.Success -> {
                    // Lista de transacciones
                    if (filteredTransactions.isEmpty()) {
                        // Mensaje cuando no hay transacciones
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "No se encontraron transacciones",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (searchQuery.isNotEmpty())
                                        "Intenta con otros términos de búsqueda"
                                    else
                                        "Agrega una nueva transacción para comenzar",
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            groupedTransactions.forEach { (date, transactions) ->
                                item {
                                    // Encabezado de grupo por fecha
                                    Text(
                                        text = date,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                                    )
                                }

                                items(transactions) { transaction ->
                                    TransactionListItem(
                                        transaction = transaction,
                                        onItemClick = {
                                            navController.navigate(Screen.TransactionDetail.createRoute(transaction.id))
                                        }
                                    )
                                }
                            }

                            // Espacio adicional al final para evitar que el FAB oculte el último elemento
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
                null -> {
                    // Estado inicial
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onItemClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val amount = transaction.amount.toDoubleOrNull() ?: 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de categoría
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.category) {
                        "Alimentación" -> Icons.Default.Restaurant
                        "Transporte" -> Icons.Default.DirectionsCar
                        "Entretenimiento" -> Icons.Default.TheaterComedy
                        "Salud" -> Icons.Default.HealthAndSafety
                        "Suscripciones" -> Icons.Default.Subscriptions
                        "Trabajo" -> Icons.Default.Work
                        "Ingreso" -> Icons.Default.MonetizationOn
                        else -> Icons.Default.Payments
                    },
                    contentDescription = transaction.category,
                    tint = if (transaction.isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                )
            }

            // Información de la transacción
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = transaction.category,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Monto
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (transaction.isExpense)
                        "- ${currencyFormatter.format(amount)}"
                    else
                        "+ ${currencyFormatter.format(amount)}",
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                )

                if (!transaction.receiptUrl.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Comprobante",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}