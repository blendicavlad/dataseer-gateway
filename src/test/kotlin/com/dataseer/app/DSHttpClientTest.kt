package com.dataseer.app

import com.dataseer.app.config.HttpClientConfig
import com.dataseer.app.config.RestTemplateConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.web.client.RestTemplate


@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [HttpClientConfig::class, RestTemplateConfig::class])
class DSHttpClientTest {
    @Autowired
    lateinit var restTemplate: RestTemplate

    @Test
    fun postResponse() {
        val uri = "http://localhost:8000/api/core"
        val result = restTemplate.postForEntity(uri,null,String::class.java)
        println(result.body)
        Assert.assertTrue(result.body!!.contains("test"))
    }
}