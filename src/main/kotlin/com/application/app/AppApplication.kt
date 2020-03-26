package com.application.app

import com.application.app.config.AppProperties
import com.application.app.config.FileStorageProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class, FileStorageProperties::class)
class AppApplication

fun main(args: Array<String>) {
	runApplication<AppApplication>(*args)


}
