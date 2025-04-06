package com.example.mydeal.view.screens.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydeal.ui.theme.*
import com.example.mydeal.view.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
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