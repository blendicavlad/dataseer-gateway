package com.dataseer.app.security

import com.dataseer.app.model.User
import com.dataseer.app.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import kotlin.Exception

/**
 * Provides the current logged user from the session context
 * @author Blendica Vlad
 * @date 10.03.2020
 */
@Component
class SecurityContextProvider {

    @Autowired
    private lateinit var userRepository: UserRepository

    /**
     * Gets current logged in [UserPrincipal]
     * @return logged in user [UserPrincipal]
     */
    fun getCurrentUserPrincipal(): UserPrincipal? {

        val auth = SecurityContextHolder.getContext().authentication as AbstractAuthenticationToken
        return auth.principal as UserPrincipal
    }

    /**
     * Gets current logged in [User] of [UserPrincipal]
     * @return logged in user [User]
     */
    @Throws(Exception::class)
    fun getCurrentContextUser(): User? {

        val userPrincipal = getCurrentUserPrincipal()
        return userRepository.findById(userPrincipal?.id!!).orElseThrow {
            Exception("SECURITY VIOLATION: Could not determine current user!")
        }
    }
}