package com.dataseer.app

import com.dataseer.app.controller.AuthController
import com.dataseer.app.payload.LoginRequest
import com.dataseer.app.service.SecureDataSetStorageService
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


//@ContextConfiguration(classes = [HttpClientConfig::class, RestTemplateConfig::class])
@SpringBootTest
@RunWith(SpringRunner::class)
class DSHttpClientTest {
    @Autowired
    lateinit var restTemplate: RestTemplate
    @Autowired
    lateinit var dataSetStorageService: SecureDataSetStorageService
    @Autowired
    lateinit var authController: AuthController

    @Test
    fun postResponse() {
        val uri = "http://localhost:8000/api/core/describe"
        val loginRequest : LoginRequest = LoginRequest("andrei@gmail.com", "parola")
        authController.authenticateUser(loginRequest)
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val fileMap = LinkedMultiValueMap<String, String>()
        val body = LinkedMultiValueMap<String, Any>()
        val contentDisposition: ContentDisposition = ContentDisposition
                .builder("text/csv")
                .name("file")
                .filename("file.csv")
                .build()
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        val fileEntity : HttpEntity<ByteArray> = HttpEntity(dataSetStorageService.retrieveDataSet(17).get().data!!, fileMap)
        body.add("file", fileEntity)
        val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(body, headers)
        try {
            val response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    requestEntity,
                    String::class.java)
                    println(response.body)
        } catch (e: HttpClientErrorException) {
            e.printStackTrace()
        }
        Assert.assertTrue(true)
    }
}