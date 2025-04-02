package com.example.mydeal.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

data class ExpenseCategory(
    val name: String,
    val amount: Double,
    val color: Color
)

@Composable
fun ExpenseChart(
    totalAmount: Double,
    categories: List<ExpenseCategory>,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Distribución de Gastos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Gráfico circular
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    categories = categories,
                    totalAmount = totalAmount
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = currencyFormatter.format(totalAmount),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leyenda
            categories.forEach { category ->
                val percentage = (category.amount / totalAmount * 100).toInt()

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
                            .background(category.color)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = currencyFormatter.format(category.amount),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "$percentage%",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    categories: List<ExpenseCategory>,
    totalAmount: Double
) {
    Canvas(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
    ) {
        val strokeWidth = 30.dp.toPx()
        val canvasSize = size.minDimension
        val radius = (canvasSize - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        var startAngle = -90f

        categories.forEach { category ->
            val sweepAngle = (category.amount / totalAmount * 360f).toFloat()

            drawArc(
                color = category.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
fun ExpenseBarChart(
    categories: List<ExpenseCategory>,
    maxAmount: Double,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Gastos por Categoría",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            categories.forEach { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        modifier = Modifier.width(100.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(16.dp)
                    ) {
                        // Barra de fondo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        )

                        // Barra de progreso
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((category.amount / maxAmount).toFloat())
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(category.color)
                        )
                    }

                    Text(
                        text = currencyFormatter.format(category.amount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(100.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlySummaryChart(
    monthsData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val maxAmount = monthsData.maxOfOrNull { it.second } ?: 0.0
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Gastos Mensuales",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barWidth = canvasWidth / monthsData.size * 0.6f
                val spaceBetween = canvasWidth / monthsData.size * 0.4f

                // Dibujar líneas de guía horizontales
                val numGuideLines = 5
                val stepSize = canvasHeight / numGuideLines

                for (i in 0..numGuideLines) {
                    val y = canvasHeight - (i * stepSize)

                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Dibujar barras
                monthsData.forEachIndexed { index, (_, amount) ->
                    val barHeight = (amount / maxAmount * canvasHeight).toFloat()
                    val barColor = Color(0xFF1976D2)
                    val barX = index * (barWidth + spaceBetween) + spaceBetween / 2

                    drawRect(
                        color = barColor,
                        topLeft = Offset(barX, canvasHeight - barHeight),
                        size = Size(barWidth, barHeight)
                    )
                }
            }

            // Etiquetas de meses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                monthsData.forEach { (month, _) ->
                    Text(
                        text = month,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Mes más alto",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    val (highestMonth, highestAmount) = monthsData.maxByOrNull { it.second }
                        ?: Pair("", 0.0)

                    Text(
                        text = "$highestMonth: ${currencyFormatter.format(highestAmount)}",
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Promedio mensual",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    val average = monthsData.sumOf { it.second } / monthsData.size

                    Text(
                        text = currencyFormatter.format(average),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}