package com.application.app.controller

import com.application.app.model.DataSet
import com.application.app.payload.ApiResponse
import com.application.app.payload.UploadFileResponse
import com.application.app.service.SecureDataSetStorageService
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
    private lateinit var secureDataSetStorageService: SecureDataSetStorageService

    @PostMapping("/upload_dataset")
    fun uploadDataSet(@RequestParam("file") file: MultipartFile,
                      @RequestParam formData : MultiValueMap<String,String>): ResponseEntity<UploadFileResponse> {

        val dataSet: DataSet = secureDataSetStorageService.storeDataSet(file, DataSet(
                fileName = formData["filename"]!![0],
                description = formData["description"]!![0]
        ))

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/dataset/download_dataset_file/")
                .path(dataSet.id.toString())
                .toUriString()

        return ResponseEntity.ok()
                .body(UploadFileResponse(dataSet.fileName, fileDownloadUri, file.contentType!!, dataSet.data?.size!!.toLong()))
    }

    @GetMapping("/download_dataset_file/{dataset_id}")
    fun downloadFile(@PathVariable dataset_id: Long): ResponseEntity<*> {

        val dataSetFile: Optional<DataSet> = secureDataSetStorageService.retrieveDataSet(dataset_id)

        return if (dataSetFile.isPresent) {
            ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(dataSetFile.get().fileType!!))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataSetFile.get().fileName + "\"")
                    .body(ByteArrayResource(dataSetFile.get().data!!))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse(false,"No dataset with id: $dataset_id found"))
        }
    }

    @GetMapping("/get_dataset/{dataset_id}")
    fun getDataSet(@PathVariable dataset_id: Long) : ResponseEntity<*> {

        val dataSet : Optional<DataSet> = secureDataSetStorageService.retrieveDataSet(dataset_id)

        return if (dataSet.isPresent) ResponseEntity.ok().body(dataSet.get())

        else ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(false,"No dataset with id: $dataset_id found"))
    }

    @GetMapping("/get_datasets")
    fun getDataSets() : ResponseEntity<*> {

        val dataSets : List<DataSet> = secureDataSetStorageService.retrieveDataSets()

        return ResponseEntity.ok().body(dataSets)
    }
}