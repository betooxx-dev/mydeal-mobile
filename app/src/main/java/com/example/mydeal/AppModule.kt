package com.example.mydeal

import android.app.Application
import com.example.mydeal.model.service.AuthService
import com.example.mydeal.model.service.TransactionService
import com.example.mydeal.viewmodel.LoginViewModel
import com.example.mydeal.viewmodel.RegisterViewModel
import com.example.mydeal.viewmodel.TransactionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Primero registra la Application
    single { androidContext() as Application }

    // Servicios
    single { AuthService(androidContext()) }
    single { TransactionService(androidContext()) }

    // ViewModels
    viewModel { LoginViewModel(get<Application>()) }
    viewModel { RegisterViewModel(get<Application>()) }
    viewModel { TransactionViewModel(get<Application>()) }
}