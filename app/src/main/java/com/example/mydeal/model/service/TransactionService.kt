package com.example.mydeal.model.service

import android.content.Context
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.api.RetrofitClient
import com.example.mydeal.model.api.TransactionApi
import com.example.mydeal.model.data.CreateTransactionRequest
import com.example.mydeal.model.data.RecurringPeriod
import com.example.mydeal.model.data.TransactionResponse
import com.example.mydeal.model.data.UploadReceiptResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionService(private val context: Context) {
    private val transactionApi = RetrofitClient.getAuthInstance(context).create(TransactionApi::class.java)

    suspend fun createTransaction(
        amount: Double,
        isExpense: Boolean,
        description: String,
        category: String,
        date: String,
        isRecurring: Boolean,
        recurringPeriod: RecurringPeriod,
        receiptUrl: String? = null,
        location: String? = null
    ): ApiResponse<TransactionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val amountString = amount.toString()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateObj = dateFormat.parse(date) ?: Date()
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val formattedDate = isoFormat.format(dateObj)

                val request = CreateTransactionRequest(
                    amount = amountString,
                    isExpense = isExpense,
                    description = description,
                    category = category,
                    date = formattedDate,
                    isRecurring = isRecurring,
                    recurringPeriod = if (isRecurring) recurringPeriod else RecurringPeriod.NONE,
                    receiptUrl = receiptUrl,
                    location = location
                )

                val response = transactionApi.createTransaction(request)

                if (response.isSuccessful) {
                    ApiResponse.Success(response.body()!!)
                } else {
                    ApiResponse.Error(response.errorBody()?.string() ?: "Error al crear la transacción")
                }
            } catch (e: Exception) {
                ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun uploadReceipt(imageFile: File): ApiResponse<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

                val response = transactionApi.uploadReceipt(imagePart)

                if (response.isSuccessful) {
                    val uploadResponse = response.body()
                    if (uploadResponse != null) {
                        ApiResponse.Success(uploadResponse.secureUrl)
                    } else {
                        ApiResponse.Error("No se recibió URL de la imagen")
                    }
                } else {
                    ApiResponse.Error(response.errorBody()?.string() ?: "Error al subir la imagen")
                }
            } catch (e: Exception) {
                ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun getTransactions(): ApiResponse<List<TransactionResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = transactionApi.getTransactions()

                if (response.isSuccessful) {
                    ApiResponse.Success(response.body() ?: emptyList())
                } else {
                    ApiResponse.Error(response.errorBody()?.string() ?: "Error al obtener las transacciones")
                }
            } catch (e: Exception) {
                ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun getTransactionById(id: String): ApiResponse<TransactionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = transactionApi.getTransactionById(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        ApiResponse.Success(body)
                    } else {
                        ApiResponse.Error("No se encontró la transacción")
                    }
                } else {
                    ApiResponse.Error("Error al obtener la transacción: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun getTransactionReports(): ApiResponse<List<TransactionResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = transactionApi.getTransactionReports()

                if (response.isSuccessful) {
                    ApiResponse.Success(response.body() ?: emptyList())
                } else {
                    ApiResponse.Error(response.errorBody()?.string() ?: "Error al obtener las transacciones")
                }
            } catch (e: Exception) {
                ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

}