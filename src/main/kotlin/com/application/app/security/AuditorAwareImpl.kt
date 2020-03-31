package com.application.app.security

import com.application.app.model.User
import com.application.app.util.BeanProvider

import org.springframework.data.domain.AuditorAware
import java.util.*

class AuditorAwareImpl : AuditorAware<User> {

    private lateinit var securityContextProvider: SecurityContextProvider

    override fun getCurrentAuditor(): Optional<User> {

        securityContextProvider = BeanProvider.getBean(SecurityContextProvider::class.java)
        return Optional.of(securityContextProvider.getCurrentContextUser()!!)
    }
}