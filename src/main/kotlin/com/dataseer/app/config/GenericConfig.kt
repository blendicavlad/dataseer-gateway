package com.dataseer.app.config

import com.dataseer.app.model.User
import com.dataseer.app.security.AuditorAwareImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * Enables JPA Auditing for JPA Entities
 * @author Blendica Vlad
 * @date 01.03.2020
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class GenericConfig {

    @Bean
    fun auditorProvider() : AuditorAware<User> {
        return AuditorAwareImpl()
    }

}