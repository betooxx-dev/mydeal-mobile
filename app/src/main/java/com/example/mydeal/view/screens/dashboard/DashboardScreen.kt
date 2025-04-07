package com.example.mydeal.view.screens.dashboard

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mydeal.MainActivity
import com.example.mydeal.R
import com.example.mydeal.model.service.AuthService
import com.example.mydeal.model.service.ReminderService
import com.example.mydeal.ui.theme.*
import com.example.mydeal.view.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    // Crear una instancia de AuthService
    val authService = remember { AuthService(context) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Para solicitar permisos de notificación en Android 13+
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, mostrar notificación
            mostrarNotificacionDirecta(context)
            Toast.makeText(context, "¡Permiso concedido! Mostrando notificación...", Toast.LENGTH_SHORT).show()
        } else {
            // Mostrar mensaje si no hay permiso
            scope.launch {
                snackbarHostState.showSnackbar("Se necesita permiso para mostrar notificaciones")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MyDeal",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        // Limpiar el token (cerrar sesión)
                        authService.logout()
                        // Navegar a la pantalla de login
                        navController.navigate(Screen.Login.route) {
                            // Limpiar el back stack para que el usuario no pueda
                            // volver atrás después de cerrar sesión
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightGreen,
                    titleContentColor = TextLight,
                    actionIconContentColor = TextLight
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            QuickActions(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para probar notificaciones
            Button(
                onClick = {
                    // Si es Android 13+, solicitar permiso primero
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // Ya tiene permiso, mostrar notificación
                            mostrarNotificacionDirecta(context)
                            Toast.makeText(context, "Mostrando notificación...", Toast.LENGTH_SHORT).show()
                        } else {
                            // Solicitar permiso
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        // Para versiones anteriores a Android 13
                        mostrarNotificacionDirecta(context)
                        Toast.makeText(context, "Mostrando notificación...", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Mostrar Notificación de Prueba", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón para probar el servicio de recordatorios
            Button(
                onClick = {
                    // Iniciar el servicio
                    ReminderService.startService(context)
                    Toast.makeText(context, "Servicio de recordatorios iniciado", Toast.LENGTH_SHORT).show()
                    scope.launch {
                        snackbarHostState.showSnackbar("Servicio de recordatorios activado")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Iniciar Servicio de Recordatorios", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Función simplificada para mostrar una notificación inmediata
private fun mostrarNotificacionDirecta(context: Context) {
    try {
        // Crear canal de notificación para Android 8.0+
        val channelId = "my_deal_test_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canal de Pruebas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones de prueba"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app cuando se toque la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Crear la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("📊 MyDeal Notificación")
            .setContentText("Esta es una notificación de prueba ¡Funciona correctamente!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        // Mostrar la notificación
        val notificationId = System.currentTimeMillis().toInt()

        // Verificar permisos para Android 13+
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                // No tenemos permiso, no podemos mostrar la notificación
                Log.e("Notificación", "No hay permiso para mostrar notificaciones")
                return
            }
        }

        // Enviar notificación
        NotificationManagerCompat.from(context).notify(notificationId, notification)

        // Registrar en log para depuración
        Log.d("Notificación", "Notificación enviada con ID: $notificationId")

        // Ejecutar también en el hilo principal (como respaldo)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                NotificationManagerCompat.from(context).notify(notificationId + 1, notification)
                Log.d("Notificación", "Notificación enviada desde coroutine con ID: ${notificationId + 1}")
            } catch (e: Exception) {
                Log.e("Notificación", "Error al mostrar notificación desde coroutine: ${e.message}")
            }
        }

    } catch (e: Exception) {
        Log.e("Notificación", "Error al mostrar notificación: ${e.message}")
        e.printStackTrace()

        // Mostrar Toast como fallback
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                "Error al mostrar notificación: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
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
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón de Agregar Transacción
            ActionCard(
                icon = Icons.Default.Add,
                title = "Agregar",
                subtitle = "Transacción",
                backgroundColor = LightGreen,
                modifier = Modifier
                    .weight(1f, fill = true)
                    .padding(end = 8.dp),
                onClick = { navController.navigate(Screen.AddTransaction.route) }
            )

            // Botón de Ver Transacciones
            ActionCard(
                icon = Icons.Default.List,
                title = "Ver",
                subtitle = "Transacciones",
                backgroundColor = LightGreen,
                modifier = Modifier
                    .weight(1f, fill = true)
                    .padding(start = 8.dp),
                onClick = { navController.navigate(Screen.TransactionList.route) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Reportes
        ActionCard(
            icon = Icons.Default.Assessment,
            title = "Ver",
            subtitle = "Reportes",
            backgroundColor = LightGreen,
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate(Screen.Reports.route) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Calculadora Financiera
        ActionCard(
            icon = Icons.Default.Calculate,
            title = "Calculadora",
            subtitle = "Financiera",
            backgroundColor = LightGreen,
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
                tint = TextLight,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = TextLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                color = TextLight.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}