package com.example.mydeal

import android.app.Application
import com.example.mydeal.appModule
import com.example.mydeal.feature_financial.di.financialCalculatorModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyDealApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyDealApplication)
            modules(
                listOf(
                    appModule,
                    financialCalculatorModule
                )
            )
        }
    }
}