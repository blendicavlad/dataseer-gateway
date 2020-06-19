package com.dataseer.app

import com.dataseer.app.controller.AuthController
import com.dataseer.app.controller.DSCoreController
import com.dataseer.app.exception.dscore.DSCoreException
import com.dataseer.app.model.DSCorePayload
import com.dataseer.app.payload.LoginRequest
import com.dataseer.app.service.SecureDataSetStorageService
import com.fasterxml.jackson.databind.ObjectMapper
import net.minidev.json.JSONObject
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
        Assert.assertNotNull("The response is empty", response)
        Assert.assertEquals("TEST RAW HTTP POST TO ETS DECOMPOSE", expected, actual)
    }

    @Test
    fun testEtsDecompose() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/etsdecompose.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        val response = dsCoreController
                .etsSeasonalDecompose(null, 1, JSONObject(mapOf("y" to "realdpi")))
        val actual : DSCorePayload = response.body!!
        Assert.assertNotNull("The response is empty", response)
        Assert.assertEquals("Altered response", expected, actual)
    }

    @Test
    fun testDescribeData() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/describe.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        val response = dsCoreController
                .describeDataSet(null, 1, JSONObject(mapOf("y" to "realdpi")))
        val actual : DSCorePayload = response.body!!
        Assert.assertNotNull("The response is empty", response)
        Assert.assertEquals("Altered response", expected, actual)
    }

    @Test
    fun testHpFilter() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/hp_filter.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        var response = dsCoreController
                .hpFilter(null, 1, JSONObject(mapOf("y" to "realdpi", "lamb" to 200)))
        var actual : DSCorePayload = response.body!!
        Assert.assertNotNull("The response is empty",response)
        Assert.assertNotEquals("The data must be different", response)
        response = dsCoreController.hpFilter(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "lamb" to 1600)))
        actual = response.body!!
        Assert.assertNotNull("The response is empty",response)
        Assert.assertEquals("Altered response", expected, actual)
    }

    @Test
    fun testSimpleMovingAvg() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/simple_moving_avg.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        var response = dsCoreController
                .simpleMovingAvg(null, 1, JSONObject(mapOf("y" to "realdpi", "windows" to arrayListOf(6,8))))
        Assert.assertNotNull("The response is empty",response)
        Assert.assertNotEquals("The data must be different", response)
        response = dsCoreController.simpleMovingAvg(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "windows" to arrayListOf(6,12))))
        val actual = response.body!!
        Assert.assertNotNull("The response is empty",response)
        Assert.assertEquals("Altered response", expected, actual)
    }

    @Test
    fun testExpWeightedMovingAvg() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/exp_weighted_moving_avg.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        var response = dsCoreController.expWeightedMovingAvg(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "span" to 6, "trend" to "mul", "adjust" to true)))
        Assert.assertNotNull("The response is empty",response)
        Assert.assertNotEquals("The data must be different", response)
        response = dsCoreController.expWeightedMovingAvg(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "span" to 12, "trend" to "add", "adjust" to false)))
        val actual = response.body!!
        Assert.assertNotNull("The response is empty",response)
        Assert.assertEquals("Altered response", expected, actual)
    }

    @Test
    fun simpleExpSmoothing() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/simple_exp_smoothing.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        var response = dsCoreController.simpleExpSmoothing(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "span" to 6)))
        Assert.assertNotNull("The response is empty",response)
        Assert.assertNotEquals("The data must be different", response)
        response = dsCoreController.simpleExpSmoothing(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "span" to 12)))
        val actual = response.body!!
        Assert.assertNotNull("The response is empty",response)
        Assert.assertEquals("Altered response", expected, actual)
    }

    @Test
    fun doubleExpSmoothing() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/double_exp_smoothing.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        var thrown : Boolean = false
        var response : ResponseEntity<DSCorePayload>
        try {
            response = dsCoreController.doubleExpSmoothing(
                    null,
                    1,
                    JSONObject(mapOf("y" to "realdpi", "span" to 6, "trend" to "asd")))
        } catch (e: DSCoreException) {
            thrown = true
            Assert.assertEquals("Allowed trend values are: mul (multiplicative), add (additive)", e.message)
        }
        response = dsCoreController.doubleExpSmoothing(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "span" to 6, "trend" to "add")))
        Assert.assertNotNull("The response is empty",response)
        Assert.assertNotEquals("The data must be different", response)
        response = dsCoreController.doubleExpSmoothing(
                null,
                1,
                JSONObject(mapOf("y" to "realdpi", "span" to 12, "trend" to "add")))
        val actual = response.body!!
        Assert.assertNotNull("The response is empty",response)
        Assert.assertEquals("Altered response", expected, actual)
    }

    @Test
    fun tripleExpSmoothing() {
        val mapper = ObjectMapper()
        val stream = FileReader("src/test/resources/test_responses/triple_exp_smoothing.json")
        val expected = mapper.readValue(stream, DSCorePayload::class.java)
        var thrown : Boolean = false
        var response : ResponseEntity<DSCorePayload>
        try {
            response = dsCoreController.tripleExpSmoothing(
                    null,
                    9,
                    JSONObject(mapOf("y" to "realdpi", "span" to 6, "trend" to "asd")))
        } catch (e: DSCoreException) {
            thrown = true
            Assert.assertEquals("Allowed trend values are: mul (multiplicative), add (additive)", e.message)
        }
        response = dsCoreController.tripleExpSmoothing(
                null,
                9,
                JSONObject(mapOf("y" to "realdpi", "span" to 6, "trend" to "mul", "seasonal" to "mul")))
        Assert.assertNotNull("The response is empty",response)
        Assert.assertNotEquals("The data must be different", response)
        response = dsCoreController.tripleExpSmoothing(
                null,
                9,
                JSONObject(mapOf("y" to "realdpi", "span" to 12, "trend" to "mul", "seasonal" to "mul")))
        val actual = response.body!!
        Assert.assertNotNull("The response is empty",response)
        Assert.assertEquals("Altered response", expected, actual)
    }
}