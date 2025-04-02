package com.example.mydeal.view.screens.transaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptCaptureScreen(
    transactionId: String,
    navController: NavController
) {
    // Estados para manejar el flujo de captura
    var cameraMode by remember { mutableStateOf(true) } // true = cámara, false = galería
    var captureState by remember { mutableStateOf(CaptureState.READY) }
    var processingProgress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(targetValue = processingProgress)
    val coroutineScope = rememberCoroutineScope()

    // Función para simular la captura y procesamiento
    fun captureAndProcess() {
        coroutineScope.launch {
            captureState = CaptureState.CAPTURING
            delay(1500) // Simular tiempo de captura
            captureState = CaptureState.PROCESSING

            // Simular progreso de procesamiento
            repeat(10) {
                delay(300)
                processingProgress = (it + 1) / 10f
            }

            captureState = CaptureState.COMPLETED
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Capturar Comprobante",
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
                    // Toggle entre cámara y galería
                    IconButton(
                        onClick = { cameraMode = !cameraMode }
                    ) {
                        Icon(
                            imageVector = if (cameraMode) Icons.Default.PhotoLibrary else Icons.Default.CameraAlt,
                            contentDescription = if (cameraMode) "Galería" else "Cámara"
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (captureState) {
                CaptureState.READY -> {
                    // Pantalla de cámara
                    CameraPreviewScreen(
                        cameraMode = cameraMode,
                        onCapture = { captureAndProcess() }
                    )
                }

                CaptureState.CAPTURING -> {
                    // Pantalla de captura en progreso
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                CaptureState.PROCESSING -> {
                    // Pantalla de procesamiento OCR
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Procesando comprobante",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Estamos extrayendo la información de tu comprobante",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            color = Color.Gray
                        )
                    }
                }

                CaptureState.COMPLETED -> {
                    // Pantalla de resultado
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Aquí iría la imagen capturada
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = null,
                                        modifier = Modifier.size(100.dp),
                                        tint = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Información extraída",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        // Información extraída del comprobante
                                        InfoRow(label = "Establecimiento", value = "Supermercado La Placita")
                                        InfoRow(label = "Fecha", value = "26/03/2025")
                                        InfoRow(label = "Hora", value = "14:35")
                                        InfoRow(label = "Total", value = "$1,250.00 MXN")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomButton(
                                text = "Volver a capturar",
                                onClick = {
                                    captureState = CaptureState.READY
                                    processingProgress = 0f
                                },
                                isSecondary = true,
                                modifier = Modifier.weight(1f)
                            )

                            CustomButton(
                                text = "Guardar",
                                onClick = {
                                    // Navegar de vuelta a los detalles de la transacción
                                    navController.navigate(Screen.TransactionDetail.createRoute(transactionId)) {
                                        popUpTo(Screen.TransactionDetail.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreviewScreen(
    cameraMode: Boolean,
    onCapture: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Simulación de vista previa de la cámara
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Overlay para guiar al usuario
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(0.7f)
                    .align(Alignment.Center)
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                // Guías de las esquinas
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopStart)
                        .border(
                            width = 2.dp,
                            color = Color.White
                        )
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .border(
                            width = 2.dp,
                            color = Color.White
                        )
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomStart)
                        .border(
                            width = 2.dp,
                            color = Color.White
                        )
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                        .border(
                            width = 2.dp,
                            color = Color.White
                        )
                )
            }

            // Instrucciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = if (cameraMode)
                        "Centra el comprobante dentro del marco"
                    else
                        "Selecciona una imagen de tu galería",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Flash (solo en modo cámara)
                if (cameraMode) {
                    IconButton(
                        onClick = { /* Toggle flash */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    // Espacio en blanco para mantener el diseño
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Botón de captura principal
                IconButton(
                    onClick = onCapture,
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (cameraMode) Icons.Default.CameraAlt else Icons.Default.PhotoLibrary,
                        contentDescription = "Capturar",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Botón para cambiar cámara (solo en modo cámara)
                if (cameraMode) {
                    IconButton(
                        onClick = { /* Switch camera */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Cambiar cámara",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    // Espacio en blanco para mantener el diseño
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            fontWeight = FontWeight.Normal
        )
    }
}

enum class CaptureState {
    READY,       // Esperando capturar
    CAPTURING,   // En proceso de captura
    PROCESSING,  // Procesando la imagen
    COMPLETED    // Procesamiento completado
}