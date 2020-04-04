package com.application.app.payload

/**
 * Standard data sturcture of an Http response
 * @author Blendica Vlad
 * @date 02.03.2020
 */
data class ApiResponse(
        val success: Boolean,
        val message: String
)