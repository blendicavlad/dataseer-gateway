package com.dataseer.app

import com.dataseer.app.config.AppProperties
import com.dataseer.app.config.FileStorageProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class, FileStorageProperties::class)
@EnableScheduling
class DataSeer

fun main(args: Array<String>) {

	runApplication<DataSeer>(*args)

}
