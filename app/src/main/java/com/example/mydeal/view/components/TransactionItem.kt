package com.example.mydeal.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionItem(
    id: String,
    amount: Double,
    category: String,
    description: String,
    date: String,
    isExpense: Boolean,
    hasReceipt: Boolean = false,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono representando el tipo de transacción
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpense) Icons.Default.ShoppingCart else Icons.Default.Payments,
                    contentDescription = if (isExpense) "Gasto" else "Ingreso",
                    tint = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                )
            }

            // Información de la transacción
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = description,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = date,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            // Monto
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                        .format(amount),
                    color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (hasReceipt) {
                    Text(
                        text = "Con comprobante",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Flecha para indicar que se puede ver más detalles
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ver detalles",
                tint = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}