package com.dataseer.app.controller

import com.dataseer.app.exception.BadRequestException
import com.dataseer.app.model.AuthProvider
import com.dataseer.app.model.User
import com.dataseer.app.payload.ApiResponse
import com.dataseer.app.payload.AuthResponse
import com.dataseer.app.payload.LoginRequest
import com.dataseer.app.payload.SignUpRequest
import com.dataseer.app.repository.UserRepository
import com.dataseer.app.security.TokenProvider
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

/**
 * Authentication REST Controller
 * @author Blendica Vlad
 * @date 02.03.2020
 */
@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired lateinit var authenticationManager : AuthenticationManager

    @Autowired lateinit var userRepository: UserRepository

    @Autowired lateinit var passwordEncoder : PasswordEncoder

    @Autowired lateinit var tokenProvider: TokenProvider

    /**
     * Authenticate the requesting user
     * @param loginRequest [LoginRequest]
     * @return authResponse [AuthResponse]
     */
    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest : LoginRequest) : ResponseEntity<AuthResponse> {

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

    /**
     * Register the requesting user
     * @param signUpRequest [SignUpRequest]
     * @return api response [ApiResponse]
     */
    @PostMapping("/signup")
    @Throws(BadRequestException::class)
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest) : ResponseEntity<ApiResponse> {

        if(userRepository.existsByEmail(signUpRequest.email))
            throw BadRequestException("Email address already in use.")

        val user = User(
                name = signUpRequest.name,
                email = signUpRequest.email,
                password = passwordEncoder.encode(signUpRequest.password),
                provider = AuthProvider.local
        )

        val result = userRepository.save(user)

        val location : URI = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location)
                .body(ApiResponse(true, "User registered successfully"))
    }
}