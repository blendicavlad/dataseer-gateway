package com.dataseer.app.payload

/**
 * Http response containing the Jwt Token and its type
 * @author Blendica Vlad
 * @date 02.03.2020
 */
data class RegisterResponse(
        val accessToken: String?,
        val success: Boolean,
        val message: String
) {
    val tokenType = "Bearer"
}