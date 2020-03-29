package com.application.app.config

import com.application.app.model.User
import com.application.app.security.AuditorAwareImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class GenericConfig {

    @Bean
    fun auditorProvider() : AuditorAware<User> {
        return AuditorAwareImpl()
    }

}