package com.application.app.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class LoginRequest(
        @NotBlank
        @Email
        var email : String,
        @NotBlank
        var password : String
)
