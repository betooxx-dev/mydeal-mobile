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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.navigation.Screen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    navController: NavController
) {
    // En una implementación real, estos datos vendrían del ViewModel
    // Datos simulados para demostración
    val amount = 1250.00
    val isExpense = true
    val description = "Compras del supermercado"
    val category = "Alimentación"
    val date = "26 de marzo, 2025"
    val time = "13:45"
    val location = "Plaza Las Américas, Chiapas"
    val hasReceipt = true
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

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

                    IconButton(onClick = { navController.navigate(Screen.EditTransaction.createRoute(transactionId)) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
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
            // Encabezado con monto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isExpense) "Gasto" else "Ingreso",
                        color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currencyFormatter.format(amount),
                        color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047),
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                        )
                    ) {
                        Text(
                            text = category,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 14.sp
                        )
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
                        value = description,
                        iconTint = Color(0xFF1976D2)
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Fecha y hora
                    DetailItem(
                        icon = Icons.Default.DateRange,
                        title = "Fecha y hora",
                        value = "$date · $time",
                        iconTint = Color(0xFF1976D2)
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Ubicación
                    DetailItem(
                        icon = Icons.Default.LocationOn,
                        title = "Ubicación",
                        value = location,
                        iconTint = Color(0xFF43A047)
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // ID de transacción
                    DetailItem(
                        icon = Icons.Default.Info,
                        title = "ID de Transacción",
                        value = "#$transactionId",
                        iconTint = Color(0xFF1976D2)
                    )
                }
            }

            // Comprobante (si existe)
            if (hasReceipt) {
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

                        // Aquí iría la imagen del comprobante
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.LightGray)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    // Aquí iría la lógica para cambiar el comprobante
                                    navController.navigate(Screen.ReceiptCapture.createRoute(transactionId))
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1976D2)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cambiar Comprobante")
                            }

                            Button(
                                onClick = {
                                    // Aquí iría la lógica para compartir el comprobante
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7E57C2)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compartir")
                            }
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
                                tint = Color(0xFF1976D2),
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
                                navController.navigate(Screen.ReceiptCapture.createRoute(transactionId))
                            }
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
                    text = "Editar",
                    onClick = {
                        navController.navigate(Screen.EditTransaction.createRoute(transactionId))
                    },
                    modifier = Modifier.weight(1f)
                )

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

        // Diálogo de confirmación para eliminar
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Eliminar Transacción") },
                text = { Text("¿Estás seguro de que quieres eliminar esta transacción? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            // Lógica para eliminar la transacción
                            showDeleteConfirmation = false
                            navController.navigate(Screen.TransactionList.route) {
                                popUpTo(Screen.TransactionList.route) {
                                    inclusive = true
                                }
                            }
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