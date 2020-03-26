package com.application.app.controller

import com.application.app.model.DataSet
import com.application.app.payload.UploadFileResponse
import com.application.app.service.DBDataSetStorageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/dataset")
class DataSetController {

    private val logger: Logger = LoggerFactory.getLogger(DataSetController::class.java)

    @Autowired
    private val dbDataSetStorageService: DBDataSetStorageService? = null

    @PostMapping("/uploadFile")
    fun uploadDataSet(@RequestParam("file") file: MultipartFile, @RequestParam formData : MultiValueMap<String,String>): ResponseEntity<UploadFileResponse> {

        val dataSet: DataSet = dbDataSetStorageService!!.storeFile(file, DataSet(
                fileName = formData["filename"]!![0],
                description = formData["description"]!![0]
        ))

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(dataSet.fileName)
                .toUriString()

        return ResponseEntity.ok()
                .body(UploadFileResponse(dataSet.fileName, fileDownloadUri,
                file.contentType!!, file.size))
    }

    @GetMapping("/download_data_set_file/{dataset_id}")
    fun downloadFile(@PathVariable dataset_id: Long): ResponseEntity<Resource> {
        // Load file from database
        val dataSet: DataSet = dbDataSetStorageService?.getFile(dataset_id)!!

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dataSet.fileType!!))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataSet.fileName + "\"")
                .body(ByteArrayResource(dataSet.data!!))
    }
}