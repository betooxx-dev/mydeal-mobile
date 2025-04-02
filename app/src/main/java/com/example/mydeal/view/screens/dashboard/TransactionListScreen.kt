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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydeal.view.components.TransactionItem
import com.example.mydeal.view.navigation.Screen
import java.text.NumberFormat
import java.util.Locale

data class TransactionData(
    val id: String,
    val amount: Double,
    val description: String,
    val category: String,
    val date: String,
    val isExpense: Boolean,
    val hasReceipt: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(navController: NavController) {
    // Estados para la UI
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var showFilterOptions by remember { mutableStateOf(false) }

    // Datos simulados de transacciones
    val transactionsList = remember {
        listOf(
            TransactionData(
                id = "1",
                amount = 1250.00,
                description = "Supermercado",
                category = "Alimentación",
                date = "26 Mar, 2025",
                isExpense = true,
                hasReceipt = true
            ),
            TransactionData(
                id = "2",
                amount = 5000.00,
                description = "Transferencia recibida",
                category = "Ingreso",
                date = "25 Mar, 2025",
                isExpense = false
            ),
            TransactionData(
                id = "3",
                amount = 450.50,
                description = "Restaurante",
                category = "Alimentación",
                date = "23 Mar, 2025",
                isExpense = true,
                hasReceipt = true
            ),
            TransactionData(
                id = "4",
                amount = 350.00,
                description = "Gasolina",
                category = "Transporte",
                date = "21 Mar, 2025",
                isExpense = true
            ),
            TransactionData(
                id = "5",
                amount = 1200.00,
                description = "Renta de oficina",
                category = "Trabajo",
                date = "20 Mar, 2025",
                isExpense = true,
                hasReceipt = true
            ),
            TransactionData(
                id = "6",
                amount = 3500.00,
                description = "Pago de cliente",
                category = "Ingreso",
                date = "18 Mar, 2025",
                isExpense = false,
                hasReceipt = true
            ),
            TransactionData(
                id = "7",
                amount = 899.99,
                description = "Cena con amigos",
                category = "Entretenimiento",
                date = "15 Mar, 2025",
                isExpense = true
            ),
            TransactionData(
                id = "8",
                amount = 599.99,
                description = "Netflix anual",
                category = "Suscripciones",
                date = "12 Mar, 2025",
                isExpense = true
            ),
            TransactionData(
                id = "9",
                amount = 320.00,
                description = "Medicinas",
                category = "Salud",
                date = "10 Mar, 2025",
                isExpense = true,
                hasReceipt = true
            ),
            TransactionData(
                id = "10",
                amount = 2500.00,
                description = "Bono trimestral",
                category = "Ingreso",
                date = "08 Mar, 2025",
                isExpense = false
            )
        )
    }

    // Filtro de transacciones
    val filteredTransactions = when (selectedFilter) {
        "Gastos" -> transactionsList.filter { it.isExpense }
        "Ingresos" -> transactionsList.filter { !it.isExpense }
        "Con comprobantes" -> transactionsList.filter { it.hasReceipt }
        else -> transactionsList
    }.filter {
        if (searchQuery.isEmpty()) true
        else it.description.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    // Agrupación por fecha
    val groupedTransactions = filteredTransactions.groupBy { it.date }

    // Calcular totales
    val totalIncome = transactionsList.filter { !it.isExpense }.sumOf { it.amount }
    val totalExpense = transactionsList.filter { it.isExpense }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

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
                        containerColor = MaterialTheme.colorScheme.primary,
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
                containerColor = MaterialTheme.colorScheme.primary
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
    }
}

@Composable
fun TransactionListItem(
    transaction: TransactionData,
    onItemClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

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
                        "- ${currencyFormatter.format(transaction.amount)}"
                    else
                        "+ ${currencyFormatter.format(transaction.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                )

                if (transaction.hasReceipt) {
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