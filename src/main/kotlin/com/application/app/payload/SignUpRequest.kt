package com.application.app.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

/**
 * Http Request defining the sign up data structure
 * @author Blendica Vlad
 * @date 02.03.2020
 */
data class SignUpRequest(
        @NotBlank
        var name : String,
        @NotBlank
        @Email
        var email : String,
        @NotBlank
        var password : String
)