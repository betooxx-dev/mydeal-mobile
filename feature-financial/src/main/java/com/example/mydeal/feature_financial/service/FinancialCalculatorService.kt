package com.example.mydeal.feature_financial.service

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

    fun calculateCompoundInterest(principal: Double, rate: Double, years: Int): Double {
        val r = rate / 100.0
        return principal * (1 + r).pow(years)
    }

    fun calculateLoanPayment(principal: Double, rate: Double, years: Int): Double {
        val monthlyRate = rate / 100.0 / 12.0
        val numberOfPayments = years * 12.0
        return principal * (monthlyRate * (1 + monthlyRate).pow(numberOfPayments)) /
                ((1 + monthlyRate).pow(numberOfPayments) - 1)
    }

    fun calculateSavingProjection(monthlySaving: Double, rate: Double, years: Int): Double {
        val monthlyRate = rate / 100.0 / 12.0
        val months = years * 12
        var total = 0.0

        for (i in 0 until months) {
            total = (total + monthlySaving) * (1 + monthlyRate)
        }

        return total
    }

    fun formatCurrency(amount: Double): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        return currencyFormatter.format(amount)
    }
}