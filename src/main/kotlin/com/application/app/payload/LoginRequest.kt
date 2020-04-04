package com.application.app.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

/**
 * Http Request defining the authentication data structure needed for login
 * @author Blendica Vlad
 * @date 02.03.2020
 */
data class LoginRequest(
        @NotBlank
        @Email
        var email : String,
        @NotBlank
        var password : String
)
