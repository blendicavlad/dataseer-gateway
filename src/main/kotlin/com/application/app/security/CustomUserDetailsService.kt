package com.application.app.security

import com.application.app.exception.ResourceNotFoundException
import com.application.app.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService : UserDetailsService {

    @Autowired lateinit var userRepository : UserRepository;

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {

        val user = userRepository.findByEmail(email) ?: throw UsernameNotFoundException("User not found with email $email")
        return UserPrincipal.create(user)
    }

    @Transactional
    @Throws(ResourceNotFoundException::class)
    fun loadUserById(id : Long) : UserDetails {

        val user = userRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("User", "id", id)

        return UserPrincipal.create(user)
    }
}