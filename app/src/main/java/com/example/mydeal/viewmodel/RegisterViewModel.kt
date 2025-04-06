package com.example.mydeal.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.service.AuthService
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val authService = AuthService(application.applicationContext)

    private val _registerResult = MutableLiveData<ApiResponse<String>>()
    val registerResult: LiveData<ApiResponse<String>> = _registerResult

    fun register(name: String, email: String, password: String) {
        _registerResult.value = ApiResponse.Loading

        viewModelScope.launch {
            val result = authService.register(name, email, password)
            _registerResult.postValue(result)
        }
    }

    fun isAuthenticated(): Boolean {
        return authService.isAuthenticated()
    }
}