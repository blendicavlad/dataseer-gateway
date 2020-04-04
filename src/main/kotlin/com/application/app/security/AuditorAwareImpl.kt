package com.application.app.security

import com.application.app.model.User
import com.application.app.util.BeanProvider

import org.springframework.data.domain.AuditorAware
import java.util.*

/**
 * Enables auditing by [User]
 * @author Blendica Vlad
 * @date 14.03.2020
 */
class AuditorAwareImpl : AuditorAware<User> {

    private lateinit var securityContextProvider: SecurityContextProvider

    /**
     * Gets current [User] from the security context, to be used in auditing (createdBy,updatedBy)
     * @return Optionl<[User]>
     */
    override fun getCurrentAuditor(): Optional<User> {
        //Being a class out of Spring Context we need to manually inject it from there
        securityContextProvider = BeanProvider.getBean(SecurityContextProvider::class.java)
        return Optional.of(securityContextProvider.getCurrentContextUser()!!)
    }
}