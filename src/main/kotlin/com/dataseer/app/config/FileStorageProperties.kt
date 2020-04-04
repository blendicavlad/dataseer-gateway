package com.dataseer.app.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Links the properties defined in the application configuration with the Spring Context
 * @author Blendica Vlad
 * @date 01.03.2020
 */
@ConfigurationProperties(prefix = "file")
open class FileStorageProperties {
    lateinit var uploadDir : String
}