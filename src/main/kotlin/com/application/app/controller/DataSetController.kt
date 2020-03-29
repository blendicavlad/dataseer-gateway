package com.application.app.controller

import com.application.app.model.DataSet
import com.application.app.payload.ApiResponse
import com.application.app.payload.UploadFileResponse
import com.application.app.repository.DataSetRepository
import com.application.app.repository.query_specifications.DataSetSpecifications
import com.application.app.security.SecurityContextProvider
import com.application.app.service.DataSetFileStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/dataset")
class DataSetController {

    @Autowired
    private lateinit var dataSetFileStorageService: DataSetFileStorageService

    @Autowired
    private lateinit var dataSetRepository : DataSetRepository

    @Autowired
    private lateinit var securityContextProvider: SecurityContextProvider

    @PostMapping("/upload_dataset")
    fun uploadDataSet(@RequestParam("file") file: MultipartFile,
                      @RequestParam formData : MultiValueMap<String,String>): ResponseEntity<UploadFileResponse> {

        val dataSet: DataSet = dataSetFileStorageService.storeDataSet(file, DataSet(
                fileName = formData["filename"]!![0],
                description = formData["description"]!![0]
        ))

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/dataset/download_dataset_file/")
                .path(dataSet.id.toString())
                .toUriString()

        return ResponseEntity.ok()
                .body(UploadFileResponse(dataSet.fileName, fileDownloadUri, file.contentType!!, file.size))
    }

    @GetMapping("/download_dataset_file/{dataset_id}")
    fun downloadFile(@PathVariable dataset_id: Long): ResponseEntity<*> {

        val dataSetFile: DataSet? = dataSetFileStorageService.getFile(dataset_id)

        return if (dataSetFile != null) {
            ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(dataSetFile.fileType!!))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataSetFile.fileName + "\"")
                    .body(ByteArrayResource(dataSetFile.data!!))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse(false,"No dataset with id: $dataset_id found"))
        }
    }

    @GetMapping("/get_dataset/{dataset_id}")
    fun getDataSet(@PathVariable dataset_id: Long) : ResponseEntity<*> {

        val dataSet : Optional<DataSet> = dataSetRepository
                .findByIdAndUserdata(dataset_id, securityContextProvider.getCurrentContextUser()!!.userData!!)

        return if (dataSet.isPresent) ResponseEntity.ok().body(dataSet.get())

        else ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(false,"No dataset with id: $dataset_id found"))
    }

    @GetMapping("/get_datasets")
    fun getDataSets() : ResponseEntity<*> {

        val dataSets : List<DataSet> = dataSetRepository
                .findAll(DataSetSpecifications.ofUserDetails(securityContextProvider.getCurrentContextUser()!!.userData))

        return ResponseEntity.ok().body(dataSets)
    }
}