package com.dataseer.app.controller

import com.dataseer.app.exception.dscore.ObsVariableMissingException
import com.dataseer.app.model.DSMethod
import com.dataseer.app.service.DSCoreService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
    lateinit var dsCoreService : DSCoreService


    @PostMapping("/describe")
    fun describeDataSet(@RequestParam("file", required = false) file: MultipartFile?,
                        @RequestParam("data_set_id") dataSetID : Long,
                      @RequestParam formData : MultiValueMap<String, String>): ResponseEntity<*> {

        if (formData["y"].isNullOrEmpty()) {
            throw ObsVariableMissingException
        }
        return dsCoreService.init(formData, dataSetID, file)
                .apply(DSMethod.DESCRIBE)
    }

    @PostMapping("/ets_seasonal_decompose")
    fun etsSeasonalDecompose(@RequestParam("file", required = false) file: MultipartFile?,
                        @RequestParam("data_set_id") dataSetID : Long,
                        @RequestParam formData : MultiValueMap<String, String>): ResponseEntity<*> {

        if (formData["y"].isNullOrEmpty()) {
            throw ObsVariableMissingException
        }
        return dsCoreService.init(formData, dataSetID, file)
                .apply(DSMethod.ETS_SEASONAL_DECOMPOSE)
    }
}