package com.example.mydeal.model.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

class FinancialCalculatorService : Service() {

    private val binder = FinancialBinder()

    inner class FinancialBinder : Binder() {
        fun getService(): FinancialCalculatorService = this@FinancialCalculatorService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    // Cálculo de interés compuesto
    fun calculateCompoundInterest(principal: Double, rate: Double, years: Int, compoundingPerYear: Int = 1): Double {
        val r = rate / 100.0
        val n = compoundingPerYear.toDouble()
        val t = years.toDouble()
        return principal * (1 + r/n).pow(n * t)
    }

    // Cálculo de pago mensual para préstamo
    fun calculateLoanPayment(principal: Double, rate: Double, years: Int): Double {
        val monthlyRate = rate / 100.0 / 12.0
        val numberOfPayments = years * 12.0
        return principal * (monthlyRate * (1 + monthlyRate).pow(numberOfPayments)) /
                ((1 + monthlyRate).pow(numberOfPayments) - 1)
    }

    // Cálculo de ahorro proyectado
    fun calculateSavingProjection(monthlySaving: Double, rate: Double, years: Int): Double {
        val monthlyRate = rate / 100.0 / 12.0
        val months = years * 12
        var total = 0.0

        for (i in 0 until months) {
            total = (total + monthlySaving) * (1 + monthlyRate)
        }

        return total
    }

    // Formatear moneda
    fun formatCurrency(amount: Double): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        return currencyFormatter.format(amount)
    }

    // Cálculo de presupuesto (porcentaje de ingresos)
    fun calculateBudget(income: Double): Map<String, Double> {
        return mapOf(
            "Necesidades" to income * 0.50,    // 50% para necesidades
            "Deseos" to income * 0.30,         // 30% para deseos
            "Ahorros" to income * 0.20         // 20% para ahorros
        )
    }
}