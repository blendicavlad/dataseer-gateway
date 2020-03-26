package com.application.app.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "file")
open class FileStorageProperties {
    lateinit var uploadDir : String
}