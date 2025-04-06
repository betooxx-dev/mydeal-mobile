package com.example.mydeal.model.api

import android.content.Context
import com.example.mydeal.util.EncryptedPreferencesUtil
import com.example.mydeal.util.JwtUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val encryptedPrefs = EncryptedPreferencesUtil.getInstance(context)
        val token = encryptedPrefs.getToken()

        if (token.isEmpty() || JwtUtil.isTokenExpired(token)) {
            return chain.proceed(originalRequest)
        }

        val newRequest = addAuthHeader(originalRequest, token)
        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            encryptedPrefs.clearAll()
        }

        return response
    }

    private fun addAuthHeader(request: Request, token: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }
}