package com.example.mydeal.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydeal.model.service.FinancialCalculatorService
import kotlinx.coroutines.launch

class FinancialViewModel : ViewModel() {

    private var financialService: FinancialCalculatorService? = null
    private var bound = false

    private val _compoundInterestResult = MutableLiveData<String>()
    val compoundInterestResult: LiveData<String> = _compoundInterestResult

    private val _loanPaymentResult = MutableLiveData<String>()
    val loanPaymentResult: LiveData<String> = _loanPaymentResult

    private val _savingProjectionResult = MutableLiveData<String>()
    val savingProjectionResult: LiveData<String> = _savingProjectionResult

    private val _budgetResult = MutableLiveData<Map<String, String>>()
    val budgetResult: LiveData<Map<String, String>> = _budgetResult

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FinancialCalculatorService.FinancialBinder
            financialService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    fun bindService(context: Context) {
        Intent(context, FinancialCalculatorService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(context: Context) {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }

    fun calculateCompoundInterest(principal: Double, rate: Double, years: Int, compoundingPerYear: Int = 1) {
        viewModelScope.launch {
            financialService?.let {
                val result = it.calculateCompoundInterest(principal, rate, years, compoundingPerYear)
                _compoundInterestResult.value = it.formatCurrency(result)
            }
        }
    }

    fun calculateLoanPayment(principal: Double, rate: Double, years: Int) {
        viewModelScope.launch {
            financialService?.let {
                val monthlyPayment = it.calculateLoanPayment(principal, rate, years)
                _loanPaymentResult.value = it.formatCurrency(monthlyPayment)
            }
        }
    }

    fun calculateSavingProjection(monthlySaving: Double, rate: Double, years: Int) {
        viewModelScope.launch {
            financialService?.let {
                val projection = it.calculateSavingProjection(monthlySaving, rate, years)
                _savingProjectionResult.value = it.formatCurrency(projection)
            }
        }
    }

    fun calculateBudget(income: Double) {
        viewModelScope.launch {
            financialService?.let {
                val budget = it.calculateBudget(income)
                val formattedBudget = budget.mapValues { entry -> it.formatCurrency(entry.value) }
                _budgetResult.value = formattedBudget
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // El servicio debería desvincularse en onStop() o onDestroy() de la actividad/fragmento
    }
}