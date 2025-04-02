package com.example.mydeal.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mydeal.view.screens.auth.LoginScreen
import com.example.mydeal.view.screens.auth.RegisterScreen
import com.example.mydeal.view.screens.dashboard.DashboardScreen
import com.example.mydeal.view.screens.dashboard.FinancialCalculatorScreen
import com.example.mydeal.view.screens.dashboard.ReportScreen
import com.example.mydeal.view.screens.dashboard.TransactionListScreen
import com.example.mydeal.view.screens.transaction.AddTransactionScreen
import com.example.mydeal.view.screens.transaction.ReceiptCaptureScreen
import com.example.mydeal.view.screens.transaction.TransactionDetailScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object TransactionList : Screen("transaction_list")
    object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: String) = "transaction_detail/$transactionId"
    }
    object AddTransaction : Screen("add_transaction")
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: String) = "edit_transaction/$transactionId"
    }
    object ReceiptCapture : Screen("receipt_capture/{transactionId}") {
        fun createRoute(transactionId: String) = "receipt_capture/$transactionId"
    }
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object NotificationSettings : Screen("notification_settings")
    object FinancialCalculator : Screen("financial_calculator")
}

@Composable
fun AppNavigation(startDestination: String = Screen.Login.route) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(Screen.TransactionList.route) {
            TransactionListScreen(navController)
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            TransactionDetailScreen(transactionId, navController)
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(navController)
        }

        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            // Pasamos el ID de transacción y un flag para indicar que estamos editando
            AddTransactionScreen(
                navController = navController,
                transactionId = transactionId,
                isEditing = true
            )
        }

        composable(
            route = Screen.ReceiptCapture.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            ReceiptCaptureScreen(transactionId, navController)
        }

        composable(Screen.Reports.route) {
            ReportScreen(navController)
        }

        composable(Screen.FinancialCalculator.route) {
            FinancialCalculatorScreen(navController)
        }
    }
}