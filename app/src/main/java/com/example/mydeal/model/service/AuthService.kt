package com.example.mydeal.model.service

import android.content.Context
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.api.AuthApi
import com.example.mydeal.model.api.RetrofitClient
import com.example.mydeal.model.data.LoginRequest
import com.example.mydeal.model.data.RegisterRequest
import com.example.mydeal.util.EncryptedPreferencesUtil
import com.example.mydeal.util.JwtUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthService(private val context: Context) {
    private val authApi = RetrofitClient.getInstance().create(AuthApi::class.java)
    private val encryptedPrefs = EncryptedPreferencesUtil.getInstance(context)

    suspend fun register(name: String, email: String, password: String): ApiResponse<String> {
        return withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(name, email, password)
                val response = authApi.register(registerRequest)

                if (response.isSuccessful) {
                    val body = response.body()
                    body?.token?.let { token ->
                        encryptedPrefs.saveToken(token)
                    }
                    ApiResponse.Success(body?.message ?: "Registro exitoso")
                } else {
                    ApiResponse.Error(response.errorBody()?.string() ?: "Error en el registro")
                }
            } catch (e: Exception) {
                ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun login(email: String, password: String): ApiResponse<String> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = authApi.login(loginRequest)

                if (response.isSuccessful) {
                    val body = response.body()
                    body?.token?.let { token ->
                        encryptedPrefs.saveToken(token)
                    }
                    ApiResponse.Success(body?.message ?: "Inicio de sesión exitoso")
                } else {
                    ApiResponse.Error(response.errorBody()?.string() ?: "Error en el inicio de sesión")
                }
            } catch (e: Exception) {
                ApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun logout() {
        encryptedPrefs.clearAll()
    }

    fun isAuthenticated(): Boolean {
        val token = encryptedPrefs.getToken()
        return token.isNotEmpty() && !JwtUtil.isTokenExpired(token)
    }

    fun getAuthToken(): String {
        return "Bearer ${encryptedPrefs.getToken()}"
    }
}