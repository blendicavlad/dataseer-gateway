package com.dataseer.app

import com.dataseer.app.config.AppProperties
import com.dataseer.app.config.FileStorageProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class, FileStorageProperties::class)
class DataSeer

fun main(args: Array<String>) {

	runApplication<DataSeer>(*args)

}
