package com.example.mydeal.feature_financial.domain

import com.example.mydeal.feature_financial.data.FinancialCalculatorRepository
import java.text.NumberFormat
import java.util.Locale

class FinancialCalculatorUseCase(private val repository: FinancialCalculatorRepository) {
    fun calculateCompoundInterest(principal: Double, rate: Double, years: Int): String {
        val result = repository.calculateCompoundInterest(principal, rate, years)
        return formatCurrency(result)
    }

    fun calculateLoanPayment(principal: Double, rate: Double, years: Int): String {
        val result = repository.calculateLoanPayment(principal, rate, years)
        return formatCurrency(result)
    }

    fun calculateSavingProjection(monthlySaving: Double, rate: Double, years: Int): String {
        val result = repository.calculateSavingProjection(monthlySaving, rate, years)
        return formatCurrency(result)
    }

    private fun formatCurrency(amount: Double): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        return currencyFormatter.format(amount)
    }
}