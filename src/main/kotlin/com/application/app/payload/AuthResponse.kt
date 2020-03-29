package com.application.app.payload

data class AuthResponse(
        val accessToken: String
) {
    val tokenType = "Bearer"
}