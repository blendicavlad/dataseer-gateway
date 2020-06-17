package com.dataseer.app

import com.dataseer.app.controller.AuthController
import com.dataseer.app.controller.DSCoreController
import com.dataseer.app.model.DSCorePayload
import com.dataseer.app.payload.LoginRequest
import com.dataseer.app.service.SecureDataSetStorageService
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.tomcat.util.json.JSONParser
import org.junit.Assert
import org.junit.Before
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
import java.io.FileReader

@SpringBootTest
@RunWith(SpringRunner::class)
class DSCoreTest {
    @Autowired
    lateinit var restTemplate: RestTemplate
    @Autowired
    lateinit var dataSetStorageService: SecureDataSetStorageService
    @Autowired
    lateinit var authController: AuthController
    @Autowired
    lateinit var dsCoreController: DSCoreController


    @Before
    fun authUser() {
        val loginRequest : LoginRequest = LoginRequest("andrei@gmail.com", "parola")
        authController.authenticateUser(loginRequest)
    }

    @Test
    fun testCoreServer() {
        val uri = "http://localhost:8000/api/core/etsd"
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
        val fileEntity : HttpEntity<ByteArray> = HttpEntity(dataSetStorageService.retrieveDataSet(1).get().data!!, fileMap)
        body.add("file", fileEntity)
        body.add("y", "realdpi")
        body.add("x", "0")
        val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(body, headers)
        var response : ResponseEntity<String>? = null
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    requestEntity,
                    String::class.java)
                    println(response.body)
        } catch (e: HttpClientErrorException) {
            e.printStackTrace()
        }
        val expected = JSONParser(FileReader("src/test/resources/test_responses/etsdecompose.json")).parse()
        val actual = JSONParser(response!!.body!!.trim()).parse()
        Assert.assertNotNull(response)
        Assert.assertEquals("TEST RAW HTTP POST TO ETS DECOMPOSE", expected, actual)
    }

    @Test
    fun testEtsDecompose() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/etsdecompose.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        val response = dsCoreController.etsSeasonalDecompose(null, 1, mapOf("y" to "realdpi"))
        val actual : DSCorePayload = response.body!!
        Assert.assertNotNull(response)
        Assert.assertEquals("TEST ETS_DECOMPOSE", expected, actual)
    }

    @Test
    fun testDescribeData() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/etsdecompose.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        val response = dsCoreController.describeDataSet(null, 1, mapOf("y" to "realdpi"))
        val actual : DSCorePayload = response.body!!
        Assert.assertNotNull(response)
        Assert.assertEquals("TEST ETS_DECOMPOSE", expected, actual)
    }

    

}