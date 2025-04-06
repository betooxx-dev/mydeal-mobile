package com.example.mydeal.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.data.RecurringPeriod
import com.example.mydeal.model.data.TransactionResponse
import com.example.mydeal.model.service.TransactionService
import com.example.mydeal.util.FileUtil
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionService = TransactionService(application.applicationContext)

    private val _uploadReceiptResult = MutableLiveData<ApiResponse<String>?>()
    val uploadReceiptResult: LiveData<ApiResponse<String>?> = _uploadReceiptResult

    private val _createTransactionResult = MutableLiveData<ApiResponse<TransactionResponse>?>()
    val createTransactionResult: LiveData<ApiResponse<TransactionResponse>?> = _createTransactionResult

    private val _transactionDetail = MutableLiveData<ApiResponse<TransactionResponse>?>()
    val transactionDetail: LiveData<ApiResponse<TransactionResponse>?> = _transactionDetail

    private val _reportsResult = MutableLiveData<ApiResponse<List<TransactionResponse>>>()
    val reportsResult: LiveData<ApiResponse<List<TransactionResponse>>> = _reportsResult

    fun uploadReceipt(imageUri: Uri) {
        _uploadReceiptResult.value = ApiResponse.Loading

        viewModelScope.launch {
            try {
                // Convertir Uri a File
                val imageFile = FileUtil.getFileFromUri(getApplication(), imageUri)
                    ?: return@launch _uploadReceiptResult.postValue(ApiResponse.Error("No se pudo procesar la imagen"))

                val result = transactionService.uploadReceipt(imageFile)
                _uploadReceiptResult.postValue(result)
            } catch (e: Exception) {
                _uploadReceiptResult.postValue(ApiResponse.Error("Error: ${e.message}"))
            }
        }
    }

    fun createTransaction(
        amount: Double,
        isExpense: Boolean,
        description: String,
        category: String,
        date: String,
        isRecurring: Boolean,
        recurringPeriod: RecurringPeriod,
        receiptUrl: String? = null,
        location: String? = null
    ) {
        _createTransactionResult.value = ApiResponse.Loading

        viewModelScope.launch {
            try {
                val result = transactionService.createTransaction(
                    amount = amount,
                    isExpense = isExpense,
                    description = description,
                    category = category,
                    date = date,
                    isRecurring = isRecurring,
                    recurringPeriod = recurringPeriod,
                    receiptUrl = receiptUrl,
                    location = location
                )

                _createTransactionResult.postValue(result)
            } catch (e: Exception) {
                _createTransactionResult.postValue(ApiResponse.Error("Error: ${e.message}"))
            }
        }
    }

    fun getTransactionById(id: String) {
        _transactionDetail.value = ApiResponse.Loading

        viewModelScope.launch {
            val result = transactionService.getTransactionById(id)
            _transactionDetail.postValue(result)
        }
    }

    fun getTransactionReports() {
        _reportsResult.value = ApiResponse.Loading

        viewModelScope.launch {
            try {
                // Opción 1: Si solo tienes /transactions y no /transactions/reports
                val result = transactionService.getTransactions()
                _reportsResult.value = result

                // Opción 2: Si implementaste /transactions/reports
                // val result = transactionService.getTransactionReports()
                // _reportsResult.value = result
            } catch (e: Exception) {
                _reportsResult.value = ApiResponse.Error("Error al obtener reportes: ${e.message}")
            }
        }
    }

    fun clearResults() {
        _uploadReceiptResult.value = null
        _createTransactionResult.value = null
        _transactionDetail.value = null
    }
}