package com.dataseer.app.service

import com.dataseer.app.exception.ResourceNotFoundException
import com.dataseer.app.exception.dscore.DSCoreException
import com.dataseer.app.model.DSCorePayload
import com.dataseer.app.model.DSMethod
import com.dataseer.app.model.DataSet
import com.dataseer.app.model.MimeTypes
import com.dataseer.app.repository.query_specifications.DataSetSpecifications
import net.minidev.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.function.Function

/**
 * DataSeer Core Service
 * @author Blendica Vlad
 * @date 17.05.2020
 */
@Service
class DSCoreService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DSCoreService::class.java)
    }

    @Autowired
    private lateinit var secureDataSetStorageService: SecureDataSetStorageService

    @Autowired
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var env : Environment;

    private lateinit var dsCoreRoot : String

    private lateinit var y: String
    private lateinit var x: String
    private lateinit var file: ByteArray
    private var dataSetID: Long = 0x00
    private var lamb: Long? = null
    private var span: Long? = null
    private var windows: String? = null
    private var trend: String? = null
    private var seasonal: String? = null
    private var initialized: Boolean = false

    @Throws(DSCoreException::class)
    fun init(params: JSONObject, dataSetID: Long, file: MultipartFile?): Function<DSMethod, ResponseEntity<DSCorePayload>> {
        dsCoreRoot = "http://" + env.getProperty("core.root")!!
        validateInput(file, dataSetID, params)
        logger.trace("DSCoreService has been initialized: date - ${LocalDateTime.now()} ")
        return Function<DSMethod, ResponseEntity<DSCorePayload>> {
            getResponse(it)
        }
    }

    @Throws(Exception::class)
    private fun validateInput(file: MultipartFile?, dataSetID: Long, params: JSONObject) {
        var dataSet : DataSet? = null
        if (file == null || file.isEmpty) {
            dataSet = secureDataSetStorageService.retrieveDataSet(dataSetID).orElseThrow {
                ResourceNotFoundException("data_set", "id", dataSetID)
            }
            this.file = dataSet.data!!
        } else {
            if (!file.contentType.equals(MimeTypes.MIME_TEXT_CSV))
                throw DSCoreException("Only " + DataSetSpecifications.allowedFileTypes.toString() + " files are allowed")
            this.file = file.bytes
        }
        if (this.file.isEmpty())
            throw DSCoreException("File not found, please attach a csv file")
        this.dataSetID = dataSetID
        if (params["x"] == null) {
            val header = dataSet!!.headers.filter { it.isTimeIndex }[0].headerName
            if (header.isNullOrEmpty()) {
                this.x = "0"
            } else {
                this.x = header
            }
        } else {
            this.x = params["x"] as String
        }
        this.y = params["y"]!! as String
        this.lamb = params["lamb"] as? Long
        this.span = params["span"] as? Long
        this.windows = params["windows"] as? String
        this.trend = params["trend"] as? String
        if (this.trend != null && !(this.trend.equals("mul") || this.trend.equals("add"))) {
            throw DSCoreException("Allowed trend values are: mul (multiplicative), add (additive)")
        }
        this.seasonal = params["seasonal"] as? String
        if (this.seasonal != null && !(this.seasonal.equals("mul") || this.seasonal.equals("add"))) {
            throw DSCoreException("Allowed seasonal values are: mul (multiplicative), add (additive)")
        }
        if (this.x.isEmpty())
            throw DSCoreException("data_set_id is mandatory!")
    }

    @Throws(DSCoreException::class)
    private fun getResponse(dsMethod: DSMethod): ResponseEntity<DSCorePayload> {

        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val request = prepareRequest(dsMethod)
        val response: ResponseEntity<DSCorePayload>
        val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(request.body, headers)
        try {
            response = restTemplate.exchange(
                    request.url,
                    HttpMethod.POST,
                    requestEntity,
                    DSCorePayload::class.java)
        } catch (e: HttpClientErrorException) {
            logger.error(e.message)
            throw e
        }
        return response
    }

    private data class Request(val url: String, val body: LinkedMultiValueMap<String, Any>)

    private fun prepareRequest(dsMethod: DSMethod): Request {

        val url = this.dsCoreRoot
        val fileMap = LinkedMultiValueMap<String, String>()
        val body = LinkedMultiValueMap<String, Any>()
        val contentDisposition: ContentDisposition = ContentDisposition
                .builder("text/csv")
                .name("file")
                .filename("file.csv")
                .build()
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        val fileEntity: HttpEntity<ByteArray> = HttpEntity(this.file, fileMap)
        body.add("file", fileEntity)
        body.add("x", this.x)

        return when (dsMethod) {
            DSMethod.DESCRIBE, DSMethod.ETS_SEASONAL_DECOMPOSE -> {
                body.add("y", this.y)
                Request(url + dsMethod.value, body)
            }
            DSMethod.HODRICK_PRESCOTT_FILTER -> {
                body.add("y", this.y)
                body.add("lamb", this.lamb!!)
                Request(url + dsMethod.value, body)
            }
            DSMethod.SIMPLE_MOVING_AVERAGE -> {
                body.add("y", this.y)
                body.add("windows", this.windows!!)
                Request(url + dsMethod.value, body)
            }
            DSMethod.EXP_WEIGHTED_MOVING_AVERAGE, DSMethod.SIMPLE_EXP_SMOOTHING, DSMethod.DOUBLE_EXP_SMOOTHING, DSMethod.TRIPLE_EXP_SMOOTHING -> {
                body.add("y", this.y)
                body.add("span", this.span!!)
                if (dsMethod == DSMethod.DOUBLE_EXP_SMOOTHING) {
                    body.add("trend", this.trend!!)
                }
                if (dsMethod == DSMethod.TRIPLE_EXP_SMOOTHING) {
                    body.add("trend", this.trend!!)
                    body.add("seasonal", this.seasonal!!)
                }
                Request(url + dsMethod.value, body)
            }
            else -> throw DSCoreException("Invalid method, the available ones are: " + DSMethod.values())
        }
    }


}