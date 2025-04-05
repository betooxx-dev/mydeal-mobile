package com.example.mydeal.feature_financial.data

import kotlin.math.pow


class FinancialCalculatorRepository {
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
}