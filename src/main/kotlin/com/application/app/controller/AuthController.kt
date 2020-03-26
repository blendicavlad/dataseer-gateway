package com.application.app.controller

import com.application.app.exception.BadRequestException
import com.application.app.model.AuthProvider
import com.application.app.model.User
import com.application.app.model.UserData
import com.application.app.payload.ApiResponse
import com.application.app.payload.AuthResponse
import com.application.app.payload.LoginRequest
import com.application.app.payload.SignUpRequest
import com.application.app.repository.UserRepository
import com.application.app.security.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired lateinit var authenticationManager : AuthenticationManager

    @Autowired lateinit var userRepository: UserRepository

    @Autowired lateinit var passwordEncoder : PasswordEncoder

    @Autowired lateinit var tokenProvider: TokenProvider

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest : LoginRequest) : ResponseEntity<*> {

        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.email,
                        loginRequest.password
                )
        )
        SecurityContextHolder.getContext().authentication = authentication
        val token = tokenProvider.createToken(authentication)
        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/signup")
    @Throws(BadRequestException::class)
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest) : ResponseEntity<*> {

        if(userRepository.existsByEmail(signUpRequest.email))
            throw BadRequestException("Email address already in use.")

        val user = User(
                name = signUpRequest.name,
                email = signUpRequest.email,
                password = passwordEncoder.encode(signUpRequest.password),
                provider = AuthProvider.local
        )

        val userData = UserData(
            user = user
        )

        user.userData = userData

        val result = userRepository.save(user)

        val location : URI = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location)
                .body(ApiResponse(true, "User registered successfully"))
    }
}