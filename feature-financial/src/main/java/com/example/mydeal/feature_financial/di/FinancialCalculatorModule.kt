package com.example.mydeal.feature_financial.di

import com.example.mydeal.feature_financial.presentation.FinancialCalculatorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val financialCalculatorModule = module {
    viewModel { FinancialCalculatorViewModel() }
}