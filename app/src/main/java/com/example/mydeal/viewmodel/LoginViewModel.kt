package com.example.mydeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.service.AuthService
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authService = AuthService(application.applicationContext)

    private val _loginResult = MutableLiveData<ApiResponse<String>?>()
    val loginResult: MutableLiveData<ApiResponse<String>?> = _loginResult

    fun login(email: String, password: String) {
        _loginResult.value = ApiResponse.Loading

        viewModelScope.launch {
            val result = authService.login(email, password)
            _loginResult.postValue(result)
        }
    }

    fun isAuthenticated(): Boolean {
        return authService.isAuthenticated()
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }
}