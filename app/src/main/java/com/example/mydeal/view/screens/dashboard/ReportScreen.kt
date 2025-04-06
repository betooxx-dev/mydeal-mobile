package com.example.mydeal.view.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.mydeal.view.components.ExpenseCategory
import com.example.mydeal.view.components.ExpenseChart
import com.example.mydeal.view.components.MonthlySummaryChart
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Interface para el servicio de API - misma que en TransactionListScreen
interface ReportTransactionApi {
    @GET("transactions")
    suspend fun getTransactions(@Header("Authorization") token: String): Response<TransactionApiResponse>
}

class ReportViewModel : ViewModel() {
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
                val api = RetrofitClient.getAuthInstance(context).create(ReportTransactionApi::class.java)
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
fun ReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = viewModel()
) {
    val context = LocalContext.current
    val transactionsResult by viewModel.transactionsResult.observeAsState()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    LaunchedEffect(Unit) {
        viewModel.fetchTransactions(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reporte Financiero",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when (transactionsResult) {
            is ApiResponse.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ApiResponse.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Error: ${(transactionsResult as ApiResponse.Error).message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchTransactions(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightGreen
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is ApiResponse.Success -> {
                val transactions = (transactionsResult as ApiResponse.Success<List<Transaction>>).data

                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay transacciones para mostrar",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    ReportContent(transactions, paddingValues)
                }
            }
            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ReportContent(
    transactions: List<Transaction>,
    paddingValues: PaddingValues
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    // Calcular datos para los reportes
    val expenses = transactions.filter { it.isExpense }
    val incomes = transactions.filter { !it.isExpense }

    // Importante: Convertir String a Double ya que amount es String en Transaction
    val totalExpenses = expenses.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalIncomes = incomes.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val balance = totalIncomes - totalExpenses

    // Agrupar gastos por categoría
    val categoriesMap = expenses.groupBy { it.category }
    val expenseCategories = categoriesMap.map { (category, categoryTransactions) ->
        val amount = categoryTransactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
        val color = when (category) {
            "Alimentación" -> Color(0xFFE53935)
            "Transporte" -> Color(0xFF1976D2)
            "Entretenimiento" -> Color(0xFFFFA000)
            "Servicios" -> Color(0xFF43A047)
            else -> Color(0xFF7E57C2)
        }
        ExpenseCategory(category, amount, color)
    }.sortedByDescending { it.amount }

    // Agrupar gastos por mes
    val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMM", Locale("es", "MX"))

    val monthlyExpenses = try {
        expenses.groupBy {
            dateFormat.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date) ?: Date())
        }.mapKeys { (key, _) ->
            val date = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(key) ?: Date()
            monthFormat.format(date)
        }.mapValues { (_, monthTransactions) ->
            monthTransactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
        }.toList().sortedBy { it.first }
    } catch (e: Exception) {
        emptyList<Pair<String, Double>>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        // Resumen
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
                    text = "Resumen Financiero",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FinancialInfoItem(
                        title = "Ingresos",
                        amount = totalIncomes,
                        color = Color(0xFF43A047)
                    )

                    FinancialInfoItem(
                        title = "Gastos",
                        amount = totalExpenses,
                        color = Color(0xFFE53935)
                    )

                    FinancialInfoItem(
                        title = "Balance",
                        amount = balance,
                        color = if (balance >= 0) Color(0xFF43A047) else Color(0xFFE53935)
                    )
                }
            }
        }

        // Gráfico de distribución de gastos por categoría
        if (expenseCategories.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Distribución de Gastos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ExpenseChart(
                        totalAmount = totalExpenses,
                        categories = expenseCategories,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Gráfico de gastos mensuales
        if (monthlyExpenses.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Gastos Mensuales",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    MonthlySummaryChart(
                        monthsData = monthlyExpenses,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Categorías más gastadas
        if (expenseCategories.isNotEmpty()) {
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
                        text = "Principales Categorías de Gasto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    expenseCategories.take(3).forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(category.color)
                            )

                            Text(
                                text = category.name,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Text(
                                text = currencyFormatter.format(category.amount),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FinancialInfoItem(
    title: String,
    amount: Double,
    color: Color
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Text(
            text = currencyFormatter.format(amount),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}