package com.example.mydeal.model.api

import com.example.mydeal.model.data.AuthResponse
import com.example.mydeal.model.data.LoginRequest
import com.example.mydeal.model.data.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}