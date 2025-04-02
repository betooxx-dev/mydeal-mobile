package com.example.mydeal.ui.theme

import androidx.compose.ui.graphics.Color

// Colores principales - Paleta de verdes
val DarkGreen = Color(0xFF1B5E20)        // Color primario - Verde oscuro
val Green = Color(0xFF2E7D32)            // Variante del primario - Verde medio
val LightGreen = Color(0xFF4CAF50)       // Variante secundaria - Verde claro
val PaleGreen = Color(0xFFE8F5E9)        // Fondo claro - Verde pálido

// Colores de acento
val AccentGreen = Color(0xFF43A047)      // Color de acento - Verde intenso para destacar
val LightAccent = Color(0xFF81C784)      // Acento claro

// Colores utilitarios
val ErrorRed = Color(0xFFB71C1C)         // Color para errores y alertas
val WarningAmber = Color(0xFFFFB300)     // Color para advertencias
val TextDark = Color(0xFF212121)         // Texto oscuro
val TextLight = Color(0xFFFAFAFA)        // Texto claro
val BackgroundLight = Color(0xFFF5F5F5)  // Fondo claro
val BackgroundDark = Color(0xFF121212)   // Fondo oscuro

// Colores específicos para transacciones
val ExpenseRed = Color(0xFFB71C1C)       // Color para gastos
val IncomeGreen = Color(0xFF2E7D32)      // Color para ingresos
val ExpenseBackground = Color(0xFFFFEBEE) // Fondo para gastos
val IncomeBackground = PaleGreen         // Fondo para ingresos

// Colores para gráficos
val ChartColors = listOf(
    DarkGreen,
    Green,
    LightGreen,
    AccentGreen,
    LightAccent,
    Color(0xFF004D40),  // Verde azulado oscuro
    Color(0xFF00695C),  // Verde azulado medio
    Color(0xFF00897B),  // Verde azulado claro
    Color(0xFF26A69A),  // Verde turquesa
    Color(0xFF80CBC4)   // Verde turquesa claro
)