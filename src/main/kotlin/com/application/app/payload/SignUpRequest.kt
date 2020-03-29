package com.application.app.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class SignUpRequest(
        @NotBlank
        var name : String,
        @NotBlank
        @Email
        var email : String,
        @NotBlank
        var password : String
)