package com.example.mydeal.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

/**
 * Clase para manejar las SharedPreferences encriptadas con AndroidX Security
 */
class EncryptedPreferencesUtil(context: Context) {

    companion object {
        private const val ENCRYPTED_PREFS_FILE_NAME = "com.example.mydeal.encrypted_prefs"
        private const val USER_TOKEN_KEY = "user_token"
        private const val USER_ID_KEY = "user_id"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_NAME_KEY = "user_name"
        private const val IS_LOGGED_IN_KEY = "is_logged_in"

        @Volatile
        private var INSTANCE: EncryptedPreferencesUtil? = null

        fun getInstance(context: Context): EncryptedPreferencesUtil {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EncryptedPreferencesUtil(context).also { INSTANCE = it }
            }
        }
    }

    // Crear la MasterKey para encriptar las preferencias
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Inicializar las SharedPreferences encriptadas
    private val encryptedSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        ENCRYPTED_PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda el token de autenticación
     */
    fun saveToken(token: String) {
        encryptedSharedPreferences.edit() { putString(USER_TOKEN_KEY, token) }
    }

    /**
     * Retorna el token de autenticación
     */
    fun getToken(): String {
        return encryptedSharedPreferences.getString(USER_TOKEN_KEY, "") ?: ""
    }

    /**
     * Guarda la información del usuario
     */
    fun saveUserInfo(userId: String, email: String, name: String) {
        encryptedSharedPreferences.edit() {
            putString(USER_ID_KEY, userId)
                .putString(USER_EMAIL_KEY, email)
                .putString(USER_NAME_KEY, name)
                .putBoolean(IS_LOGGED_IN_KEY, true)
        }
    }

    /**
     * Retorna si el usuario está logueado
     */
    fun isLoggedIn(): Boolean {
        return encryptedSharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)
    }

    /**
     * Retorna el nombre del usuario
     */
    fun getUserName(): String {
        return encryptedSharedPreferences.getString(USER_NAME_KEY, "") ?: ""
    }

    /**
     * Retorna el email del usuario
     */
    fun getUserEmail(): String {
        return encryptedSharedPreferences.getString(USER_EMAIL_KEY, "") ?: ""
    }

    /**
     * Retorna el ID del usuario
     */
    fun getUserId(): String {
        return encryptedSharedPreferences.getString(USER_ID_KEY, "") ?: ""
    }

    /**
     * Limpia todos los datos guardados (logout)
     */
    fun clearAll() {
        encryptedSharedPreferences.edit().clear().apply()
    }
}