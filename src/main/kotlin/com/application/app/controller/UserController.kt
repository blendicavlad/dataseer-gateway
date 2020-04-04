package com.application.app.controller

import com.application.app.exception.ResourceNotFoundException
import com.application.app.model.User
import com.application.app.repository.UserRepository
import com.application.app.security.CurrentUser
import com.application.app.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * User REST Controller
 * @author Blendica Vlad
 * @date 02.03.2020
 */
@RestController
class UserController {

    @Autowired lateinit var userRepository: UserRepository

    /**
     * Gets the logged-in user from the current session
     * @param userPrincipal [UserPrincipal]
     * @return [User]
     * @throws [ResourceNotFoundException]
     */
    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    @Throws(ResourceNotFoundException::class)
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal) : User {

        return userRepository.findByIdOrNull(userPrincipal.id) ?: throw ResourceNotFoundException("User","id", userPrincipal.id)
    }
}