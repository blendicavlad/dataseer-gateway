package com.dataseer.app.service

import com.dataseer.app.exception.ResourceNotFoundException
import com.dataseer.app.exception.dscore.DSCoreException
import com.dataseer.app.exception.dscore.ObsVariableMissingException
import com.dataseer.app.model.DSCorePayload
import com.dataseer.app.model.DSMethod
import com.dataseer.app.model.DataSet
import com.dataseer.app.model.MimeTypes
import com.dataseer.app.repository.query_specifications.DataSetSpecifications
import net.minidev.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.core.env.Environment
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.multipart.MultipartFile
import java.lang.ClassCastException
import java.time.LocalDateTime
import java.util.function.Function

/**
 * DataSeer Core Service
 * @author Blendica Vlad
 * @date 17.05.2020
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class DSCoreService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DSCoreService::class.java)
    }

    @Autowired
    private lateinit var secureDataSetStorageService: SecureDataSetStorageService

    @Autowired
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var env : Environment

    private lateinit var dsCoreRoot : String

    private lateinit var y: String
    private lateinit var x: String
    private lateinit var file: ByteArray
    private var dataSetID: Long = 0L
    private var lamb: Int? = 1600
    private var span: Int? = null
    private var windows: List<Int>? = null
    private var adjust = false
    private var trend: String? = null
    private var seasonal: String? = null
    private var initialized: Boolean = false
    private var freq: String? = null
    private var forecastPeriods: Int? = null
    private var model: String? = "mul"
    private var method: String? = "mle"
    private var trainPercent: Int? = 80

    @Throws(DSCoreException::class)
    fun init(params: JSONObject, dataSetID: Long, file: MultipartFile?): Function<DSMethod, ResponseEntity<DSCorePayload>> {
        dsCoreRoot = "http://" + env.getProperty("core.root")!!
        validateInput(file, dataSetID, params)
        logger.trace("DSCoreService has been initialized: date - ${LocalDateTime.now()} ")
        return Function {
            getResponse(it)
        }
    }

    /**
     * TODO Refactoring
     */
    @Throws(Exception::class)
    private fun validateInput(file: MultipartFile?, dataSetID: Long, params: JSONObject) {
        var dataSet : DataSet? = null
        if (params["y"].toString().isEmpty()) {
            throw ObsVariableMissingException
        }
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
        try {
            params["lamb"]?.let { this.lamb = it as Int }
            params["span"]?.let { this.span = it as Int }
            params["adjust"]?.let { this.adjust = it as Boolean }
            params["windows"]?.let { this.windows = it as List<Int> }
            params["trend"]?.let { this.trend = it as String }
            params["seasonal"]?.let { this.seasonal = it as String }
            params["freq"]?.let { this.freq = it as String }
            params["periods"]?.let { this.forecastPeriods = it as Int }
            params["model"]?.let { this.model = it as String }
            params["train_percent"]?.let { this.trainPercent = it as Int }
            params["method"]?.let { this.method = it as String }
        } catch (e: ClassCastException) {
            throw DSCoreException("One of the parameters has the wrong data type")
        }
        if (this.trend != null && !(this.trend.equals("mul") || this.trend.equals("add"))) {
            throw DSCoreException("Allowed trend values are: mul (multiplicative), add (additive)")
        }
        if (this.model != null && !(this.model.equals("mul") || this.model.equals("add"))) {
            throw DSCoreException("Allowed model values are: mul (multiplicative), add (additive)")
        }
        if (this.method != null && !(this.method.equals("mle") || this.method.equals("cmle"))) {
            throw DSCoreException("Allowed method values are: cmle (Conditional maximum likelihood using OLS)," +
                    " mle (Unconditional (exact) maximum likelihood)")
        }
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
            throw e.message?.let { DSCoreException(it) }!!
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
        body.add("y", this.y)

        return when (dsMethod) {
            DSMethod.DESCRIBE, DSMethod.ETS_SEASONAL_DECOMPOSE -> {
                body.add("freq", this.freq)
                Request(url + dsMethod.value, body)
            }
            DSMethod.HODRICK_PRESCOTT_FILTER -> {
                body.add("lamb", this.lamb!!)
                Request(url + dsMethod.value, body)
            }
            DSMethod.SIMPLE_MOVING_AVERAGE -> {
                body.add("windows", this.windows!!)
                Request(url + dsMethod.value, body)
            }
            DSMethod.EXP_WEIGHTED_MOVING_AVERAGE, DSMethod.SIMPLE_EXP_SMOOTHING, DSMethod.DOUBLE_EXP_SMOOTHING, DSMethod.TRIPLE_EXP_SMOOTHING -> {
                body.add("span", this.span!!)
                if (dsMethod == DSMethod.EXP_WEIGHTED_MOVING_AVERAGE) {
                    body.add("trend", this.trend)
                    body.add("adjust", this.adjust)
                }
                if (dsMethod == DSMethod.DOUBLE_EXP_SMOOTHING) {
                    body.add("trend", this.trend!!)
                }
                if (dsMethod == DSMethod.TRIPLE_EXP_SMOOTHING) {
                    body.add("trend", this.trend!!)
                    body.add("seasonal", this.seasonal!!)
                }
                Request(url + dsMethod.value, body)
            }
            DSMethod.HW_FORECAST, DSMethod.AUTO_REGRESSION -> {
                body.add("freq", this.freq)
                body.add("periods", this.forecastPeriods)
                body.add("train_percent", this.trainPercent)
                if (dsMethod == DSMethod.AUTO_REGRESSION) {
                    body.add("method", this.method)
                }
                if (dsMethod == DSMethod.HW_FORECAST) {
                    body.add("model", this.model)
                }
                Request(url + dsMethod.value, body)
            }
            DSMethod.ADF_TEST -> {
                Request(url + dsMethod.value, body)
            }
            else -> throw DSCoreException("Invalid method, the available ones are: " + DSMethod.values())
        }
    }


}