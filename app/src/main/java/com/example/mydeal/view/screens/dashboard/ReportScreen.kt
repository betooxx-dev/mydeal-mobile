package com.example.mydeal.view.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydeal.view.components.ExpenseCategory
import com.example.mydeal.view.components.ExpenseChart
import com.example.mydeal.view.components.ExpenseBarChart
import com.example.mydeal.view.components.MonthlySummaryChart
import com.example.mydeal.view.navigation.Screen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController) {
    // Datos simulados para los gráficos y reportes
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    // Datos para el gráfico de categorías
    val expenseCategories = listOf(
        ExpenseCategory("Alimentación", 3450.0, Color(0xFFE53935)),
        ExpenseCategory("Transporte", 1200.0, Color(0xFF1976D2)),
        ExpenseCategory("Entretenimiento", 850.0, Color(0xFFFFA000)),
        ExpenseCategory("Servicios", 1350.0, Color(0xFF43A047)),
        ExpenseCategory("Otros", 650.0, Color(0xFF7E57C2))
    )

    // Datos para el gráfico mensual
    val monthlyData = listOf(
        "Ene" to 4500.0,
        "Feb" to 5200.0,
        "Mar" to 6800.0,
        "Abr" to 4900.0,
        "May" to 5100.0,
        "Jun" to 4600.0
    )

    // Totales
    val totalExpenses = expenseCategories.sumOf { it.amount }
    val totalIncome = 25000.0
    val totalSavings = totalIncome - totalExpenses
    val savingsPercentage = (totalSavings / totalIncome * 100).toInt()

    // Selección del periodo
    val periods = listOf("Esta semana", "Este mes", "3 meses", "6 meses", "Este año")
    var selectedPeriod by remember { mutableStateOf(periods[1]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reportes y Estadísticas",
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
                    IconButton(onClick = { /* Lógica para compartir reportes */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir"
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Selector de periodo
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
                        text = "Periodo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        periods.forEach { period ->
                            FilterChip(
                                selected = selectedPeriod == period,
                                onClick = { selectedPeriod = period },
                                label = { Text(period) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Tarjetas de resumen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Ingresos",
                    amount = totalIncome,
                    icon = Icons.Default.ArrowUpward,
                    iconTint = Color(0xFF43A047),
                    containerColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Gastos",
                    amount = totalExpenses,
                    icon = Icons.Default.ArrowDownward,
                    iconTint = Color(0xFFE53935),
                    containerColor = Color(0xFFFFEBEE),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de ahorros
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = Color(0xFF1976D2)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Ahorros del periodo",
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "${savingsPercentage}% de tus ingresos",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = currencyFormatter.format(totalSavings),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (totalSavings > 0) Color(0xFF43A047) else Color(0xFFE53935)
                    )
                }

                LinearProgressIndicator(
                    progress = (totalSavings / totalIncome).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF43A047),
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }

            // Gráfico de distribución de gastos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                ExpenseChart(
                    totalAmount = totalExpenses,
                    categories = expenseCategories,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Gráfico de gastos mensuales
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                MonthlySummaryChart(
                    monthsData = monthlyData,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Sección de recomendaciones
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
                        text = "Recomendaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    RecommendationItem(
                        icon = Icons.Default.Restaurant,
                        title = "Alimentación",
                        description = "Puedes reducir tus gastos en alimentación cocinando más en casa.",
                        iconTint = Color(0xFFE53935)
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    RecommendationItem(
                        icon = Icons.Default.DirectionsCar,
                        title = "Transporte",
                        description = "Considera opciones de transporte compartido para reducir gastos.",
                        iconTint = Color(0xFF1976D2)
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    RecommendationItem(
                        icon = Icons.Default.TheaterComedy,
                        title = "Entretenimiento",
                        description = "Busca alternativas gratuitas o de bajo costo para tu entretenimiento.",
                        iconTint = Color(0xFFFFA000)
                    )
                }
            }

            // Botón para descargar reporte
            Button(
                onClick = { /* Lógica para descargar el reporte */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar Reporte")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    iconTint: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )

            Text(
                text = currencyFormatter.format(amount),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RecommendationItem(
    icon: ImageVector,
    title: String,
    description: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
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
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}