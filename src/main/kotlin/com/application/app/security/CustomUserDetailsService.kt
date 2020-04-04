package com.application.app.security

import com.application.app.exception.ResourceNotFoundException
import com.application.app.model.User
import com.application.app.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * [User] handler for the authentication process
 * @author Blendica Vlad
 * @date 03.03.2020
 */
@Service
class CustomUserDetailsService : UserDetailsService {

    @Autowired lateinit var userRepository : UserRepository;

    /**
     * Loads or creates the [User] by username
     * @param email [String]
     * @return [UserDetails]
     */
    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {

        val user = userRepository.findByEmail(email) ?: throw UsernameNotFoundException("User not found with email $email")
        return UserPrincipal.create(user)
    }

    /**
     * Loads or creates the [User] by id
     * @param id [Long]
     * @return [UserDetails]
     */
    @Transactional
    @Throws(ResourceNotFoundException::class)
    fun loadUserById(id : Long) : UserDetails {

        val user = userRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("User", "id", id)

        return UserPrincipal.create(user)
    }
}