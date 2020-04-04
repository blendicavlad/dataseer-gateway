package com.dataseer.app.config

import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configures CORS mappings
 * @author Blendica Vlad
 * @date 02.03.2020
 */
open class WebMvcConfig : WebMvcConfigurer {

    private val MAX_AGE_SECS : Long = 3600

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS)
    }
}