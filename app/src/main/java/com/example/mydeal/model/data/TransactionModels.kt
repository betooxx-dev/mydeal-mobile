package com.example.mydeal.model.data

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateTransactionRequest(
    val amount: String,
    val isExpense: Boolean,
    val description: String,
    val category: String,
    val date: String, // Formato ISO "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val isRecurring: Boolean,
    val recurringPeriod: RecurringPeriod,
    val receiptUrl: String? = null,
    val location: String? = null
)

data class UploadReceiptResponse(
    @SerializedName("secure_url")
    val secureUrl: String
)

data class TransactionResponse(
    val id: String,
    val amount: Double,
    val isExpense: Boolean,
    val description: String,
    val category: String,
    val date: String,
    val isRecurring: Boolean,
    val recurringPeriod: RecurringPeriod,
    val receiptUrl: String?,
    val location: String?
)

enum class RecurringPeriod {
    @SerializedName("none")
    NONE,
    @SerializedName("daily")
    DAILY,
    @SerializedName("weekly")
    WEEKLY,
    @SerializedName("monthly")
    MONTHLY,
    @SerializedName("yearly")
    YEARLY
}