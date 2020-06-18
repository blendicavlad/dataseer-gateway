package com.dataseer.app.controller

import com.dataseer.app.exception.dscore.ObsVariableMissingException
import com.dataseer.app.model.DSCorePayload
import com.dataseer.app.model.DSMethod
import com.dataseer.app.service.DSCoreService
import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * DataSeer Core Controller
 * @author Blendica Vlad
 * @date 17.05.2020
 */
@RestController
@RequestMapping("/core")
class DSCoreController {

    @Autowired
    lateinit var dsCoreService: DSCoreService


    @PostMapping("/describe")
    fun describeDataSet(@RequestParam("file", required = false) file: MultipartFile?,
                        @RequestParam("data_set_id") dataSetID: Long,
                        @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.DESCRIBE)
    }

    @PostMapping("/ets_seasonal_decompose")
    fun etsSeasonalDecompose(@RequestParam("file", required = false) file: MultipartFile?,
                             @RequestParam("data_set_id") dataSetID: Long,
                             @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.ETS_SEASONAL_DECOMPOSE)
    }

    @PostMapping("/hp_filter")
    fun hpFilter(@RequestParam("file", required = false) file: MultipartFile?,
                 @RequestParam("data_set_id") dataSetID: Long,
                 @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.HODRICK_PRESCOTT_FILTER)
    }

    @PostMapping("/simple_moving_avg")
    fun simpleMovingAvg(@RequestParam("file", required = false) file: MultipartFile?,
                        @RequestParam("data_set_id") dataSetID: Long,
                        @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.SIMPLE_MOVING_AVERAGE)
    }

    @PostMapping("/exp_weighted_mov_avg")
    fun expWeightedMovingAvg(@RequestParam("file", required = false) file: MultipartFile?,
                             @RequestParam("data_set_id") dataSetID: Long,
                             @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.EXP_WEIGHTED_MOVING_AVERAGE)
    }

    @PostMapping("/simple_exp_smoothing")
    fun simpleExpSmoothing(@RequestParam("file", required = false) file: MultipartFile?,
                           @RequestParam("data_set_id") dataSetID: Long,
                           @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.SIMPLE_EXP_SMOOTHING)
    }

    @PostMapping("/double_exp_smoothing")
    fun doubleExpSmoothing(@RequestParam("file", required = false) file: MultipartFile?,
                           @RequestParam("data_set_id") dataSetID: Long,
                           @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.DOUBLE_EXP_SMOOTHING)
    }

    @PostMapping("/triple_exp_smoothing")
    fun tripleExpSmoothing(@RequestParam("file", required = false) file: MultipartFile?,
                           @RequestParam("data_set_id") dataSetID: Long,
                           @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.TRIPLE_EXP_SMOOTHING)
    }

    @PostMapping("/hw_forecast")
    fun hwForecast(@RequestParam("file", required = false) file: MultipartFile?,
                   @RequestParam("data_set_id") dataSetID: Long,
                   @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.HW_FORECAST)
    }

    @PostMapping("/auto_regression")
    fun autoRegression(@RequestParam("file", required = false) file: MultipartFile?,
                       @RequestParam("data_set_id") dataSetID: Long,
                       @RequestBody body: JSONObject): ResponseEntity<DSCorePayload> {

        return dsCoreService.init(body, dataSetID, file).apply(DSMethod.SIMPLE_MOVING_AVERAGE)
    }
}