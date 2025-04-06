package com.example.mydeal.util

import android.util.Base64
import org.json.JSONObject
import java.util.Date

object JwtUtil {
    private fun parseToken(token: String): JwtClaims? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val normalized = payload.replace('-', '+').replace('_', '/')
            val decoded = Base64.decode(normalized, Base64.DEFAULT)

            val jsonObject = JSONObject(String(decoded))

            val id = jsonObject.optString("id", "")
            val issuedAt = jsonObject.optLong("iat", 0)
            val expiresAt = jsonObject.optLong("exp", 0)

            JwtClaims(id, issuedAt, expiresAt)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isTokenExpired(token: String): Boolean {
        val claims = parseToken(token) ?: return true
        val expirationTime = claims.exp * 1000
        return Date(expirationTime).before(Date())
    }
}

data class JwtClaims(
    val id: String,
    val iat: Long,  // Issued At (cuándo fue emitido)
    val exp: Long   // Expiration Time (cuándo expira)
)