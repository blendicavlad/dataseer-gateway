package com.dataseer.app.config

import org.apache.http.impl.client.CloseableHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.client.RestTemplate

/**
 * Overrides the default RestTemplate in Spring application context 
 * @author Blendica Vlad
 * @date 04.05.2020
 */
@Configuration
class RestTemplateConfig {

    @Autowired lateinit var httpClient: CloseableHttpClient

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate(clientHttpRequestFactory())
    }


    private fun clientHttpRequestFactory(): HttpComponentsClientHttpRequestFactory {
        val clientHttpRequestFactory = HttpComponentsClientHttpRequestFactory()
        clientHttpRequestFactory.httpClient = httpClient
        return clientHttpRequestFactory
    }

    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.setThreadNamePrefix("poolScheduler")
        scheduler.poolSize = 50
        return scheduler
    }
}