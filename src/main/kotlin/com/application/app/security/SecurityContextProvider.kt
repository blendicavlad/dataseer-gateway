package com.application.app.security

import com.application.app.model.User
import com.application.app.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import kotlin.Exception

@Component
class SecurityContextProvider {

    @Autowired
    private lateinit var userRepository: UserRepository

    fun getCurrentUserPrincipal() : UserPrincipal? {
        val auth = SecurityContextHolder.getContext().authentication as AbstractAuthenticationToken
        return auth.principal as UserPrincipal
    }

    @Throws(Exception::class)
    fun getCurrentContextUser() : User? {
        val userPrincipal = getCurrentUserPrincipal()
        return userRepository.findById(userPrincipal?.id!!).orElseThrow{Exception("Could not determine current user!")}
    }
}