package com.example.mydeal.model.api

import com.example.mydeal.model.data.CreateTransactionRequest
import com.example.mydeal.model.data.TransactionResponse
import com.example.mydeal.model.data.UploadReceiptResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TransactionApi {
    @POST("transactions")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): Response<TransactionResponse>

    @GET("transactions")
    suspend fun getTransactions(): Response<List<TransactionResponse>>

    @GET("transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Response<TransactionResponse>

    @Multipart
    @POST("transactions/upload")
    suspend fun uploadReceipt(@Part image: MultipartBody.Part): Response<UploadReceiptResponse>

    @GET("transactions/reports")
    suspend fun getTransactionReports(): Response<List<TransactionResponse>>
}