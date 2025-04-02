package com.example.mydeal.view.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydeal.view.navigation.Screen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    // Estado para tracking de datos (en una app real vendrían del ViewModel)
    val totalBalance = remember { mutableStateOf(24650.75) }
    val income = remember { mutableStateOf(31500.00) }
    val expenses = remember { mutableStateOf(6849.25) }

    // Formateo de moneda para MXN
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MyDeal",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Banner de balance total
            BalanceBanner(
                balance = totalBalance.value,
                income = income.value,
                expenses = expenses.value,
                currencyFormatter = currencyFormatter
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de acciones rápidas
            QuickActions(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de transacciones recientes
            RecentTransactions(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de estadísticas
            SpendingStatistics()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BalanceBanner(
    balance: Double,
    income: Double,
    expenses: Double,
    currencyFormatter: NumberFormat
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF2196F3)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Balance Total",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )

            Text(
                text = currencyFormatter.format(balance),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ingresos
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ingresos",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = currencyFormatter.format(income),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Gastos
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gastos",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = currencyFormatter.format(expenses),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActions(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Acciones Rápidas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionCard(
                icon = Icons.Default.Add,
                title = "Agregar",
                subtitle = "Transacción",
                backgroundColor = Color(0xFF1976D2),
                modifier = Modifier.weight(1f, fill = true).padding(end = 8.dp),
                onClick = { navController.navigate(Screen.AddTransaction.route) }
            )

            ActionCard(
                icon = Icons.Default.List,
                title = "Ver",
                subtitle = "Transacciones",
                backgroundColor = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f, fill = true).padding(start = 8.dp),
                onClick = { navController.navigate(Screen.TransactionList.route) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Acción rápida para ver reportes
        ActionCard(
            icon = Icons.Default.Assessment,
            title = "Ver",
            subtitle = "Reportes",
            backgroundColor = Color(0xFFFFA000),
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate(Screen.Reports.route) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de calculadora financiera
        ActionCard(
            icon = Icons.Default.Calculate,
            title = "Calculadora",
            subtitle = "Financiera",
            backgroundColor = Color(0xFF9C27B0),
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate(Screen.FinancialCalculator.route) }
        )
    }
}

@Composable
fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RecentTransactions(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transacciones Recientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = { navController.navigate(Screen.TransactionList.route) }) {
                Text(
                    text = "Ver Todas",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ejemplos de transacciones recientes
        TransactionItem(
            description = "Supermercado",
            amount = -1250.00,
            date = "Hoy, 13:45",
            onClick = { navController.navigate(Screen.TransactionDetail.createRoute("1")) }
        )

        TransactionItem(
            description = "Transferencia recibida",
            amount = 5000.00,
            date = "Ayer, 10:30",
            onClick = { navController.navigate(Screen.TransactionDetail.createRoute("2")) }
        )

        TransactionItem(
            description = "Restaurante",
            amount = -450.50,
            date = "23 Mar, 20:15",
            onClick = { navController.navigate(Screen.TransactionDetail.createRoute("3")) }
        )
    }
}

@Composable
fun TransactionItem(
    description: String,
    amount: Double,
    date: String,
    onClick: () -> Unit
) {
    val isExpense = amount < 0
    val formattedAmount = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        .format(kotlin.math.abs(amount))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = if (isExpense) "Gasto" else "Ingreso",
                    tint = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Detalles
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = description,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Monto
            Text(
                text = if (isExpense) "- $formattedAmount" else "+ $formattedAmount",
                fontWeight = FontWeight.Bold,
                color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
            )
        }
    }
}

@Composable
fun SpendingStatistics() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Distribución de Gastos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Aquí se colocaría un componente de gráfico real
        // Por ahora simulamos un gráfico con barras
        SimulatedExpenseChart()
    }
}

@Composable
fun SimulatedExpenseChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Categorías simuladas
            val categories = listOf(
                "Alimentación" to 0.35f,
                "Transporte" to 0.20f,
                "Entretenimiento" to 0.15f,
                "Servicios" to 0.25f,
                "Otros" to 0.05f
            )

            val colors = listOf(
                Color(0xFFE53935), // Rojo
                Color(0xFF1976D2), // Azul
                Color(0xFFFFA000), // Ámbar
                Color(0xFF43A047), // Verde
                Color(0xFF7E57C2)  // Morado
            )

            categories.forEachIndexed { index, (category, percentage) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(colors[index])
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = category,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${(percentage * 100).toInt()}%",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (index < categories.size - 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                LinearProgressIndicator(
                    progress = percentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colors[index],
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )

                if (index < categories.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}