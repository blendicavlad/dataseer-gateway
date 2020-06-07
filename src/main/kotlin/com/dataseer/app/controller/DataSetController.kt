package com.dataseer.app.controller

import com.dataseer.app.model.DataSet
import com.dataseer.app.payload.ApiResponse
import com.dataseer.app.payload.UploadFileResponse
import com.dataseer.app.service.SecureDataSetStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

/**
 * [DataSet] REST Controller
 * @author Blendica Vlad
 * @date 14.03.2020
 */
@RestController
@RequestMapping("/dataset")
class DataSetController {

    @Autowired
    private lateinit var secureDataSetStorageService: SecureDataSetStorageService

    /**
     * Stores the DataSet into DB
     * @param file [MultipartFile] (A CSV file to be persisted)
     * @param formData [MultiValueMap]
     * @return uploadResponse [UploadFileResponse]
     */
    @PostMapping("/upload_dataset")
    fun uploadDataSet(@RequestParam("file") file: MultipartFile,
                      @RequestParam formData : MultiValueMap<String,String>): ResponseEntity<UploadFileResponse> {

        if (file.isEmpty) {
            ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body(ApiResponse(false,"No file found in request"))
        }
        if (formData["name"] == null) {
            ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body(ApiResponse(false,"No data set name specified"))
        }
        val dataSet: DataSet = secureDataSetStorageService.storeDataSet(file, DataSet(
                fileName = StringUtils.cleanPath(file.originalFilename!!),
                name = formData["name"]!![0],
                description = formData["description"]?.get(0)
        ))
        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/dataset/download_dataset_file/")
                .path(dataSet.id.toString())
                .toUriString()

        return ResponseEntity.ok()
                .body(UploadFileResponse(dataSet.fileName, fileDownloadUri, file.contentType!!, dataSet.data?.size!!.toLong()))
    }

    /**
     * Downloads the file of a [DataSet] by ID
     * @param dataset_id [Long]
     * @return [ByteArray] of the file or [ApiResponse] not found
     */
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

    /**
     * Returns a [DataSet] by ID, does not return the file
     * @return JSON of [DataSet] or [ApiResponse] not found
     */
    @GetMapping("/get_dataset/{dataset_id}")
    fun getDataSet(@PathVariable dataset_id: Long) : ResponseEntity<*> {

        val dataSet : Optional<DataSet> = secureDataSetStorageService.retrieveDataSet(dataset_id)

        return if (dataSet.isPresent) ResponseEntity.ok().body(dataSet.get())

        else ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(false,"No dataset with id: $dataset_id found"))
    }

    /**
     * Returns all DataSets of the logged-in user
     * @return [List<[DataSet]>]
     */
    @GetMapping("/get_datasets")
    fun getDataSets() : ResponseEntity<*> {

        val dataSets : List<DataSet> = secureDataSetStorageService.retrieveDataSets()

        return ResponseEntity.ok().body(dataSets)
    }

    /**
     * Delete a dataSet
     */
    @PostMapping("/delete_dataset/{dataset_id}")
    fun deleteDataset(@PathVariable dataset_id: Long) : ResponseEntity<*> {

        val dataSet : Optional<DataSet> = secureDataSetStorageService.retrieveDataSet(dataset_id)
        return if (dataSet.isPresent) {
            secureDataSetStorageService.deleteDataSet(dataSet.get())
            ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse(true,"DataSet with id: $dataset_id has been deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse(false,"No dataset with id: $dataset_id found"))
        }
    }

    @PostMapping("/update_dataset/{dataset_id}")
    fun updateDataset(@PathVariable dataset_id: Long, @RequestParam("file") file: MultipartFile?,
                      @RequestParam formData : MultiValueMap<String,String>): ResponseEntity<*> {

        val dataSetOptional : Optional<DataSet> = secureDataSetStorageService.retrieveDataSet(dataset_id)
        val dataSet : DataSet
        return if (dataSetOptional.isPresent) {
            dataSet = dataSetOptional.get()
            if (formData["name"] != null) {
                dataSet.name = formData["name"]!![0].toString()
            }
            if (formData["description"] != null) {
                dataSet.description = formData["description"]!![0].toString()
            }
            if (file != null && !file.isEmpty) {
                dataSet.fileName = StringUtils.cleanPath(file.originalFilename!!)
            }
            secureDataSetStorageService.storeDataSet(file!!, dataSet)
            val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/dataset/download_dataset_file/")
                    .path(dataSet.id.toString())
                    .toUriString()

            return ResponseEntity.ok()
                    .body(UploadFileResponse(dataSet.fileName, fileDownloadUri, dataSet.fileType!!, dataSet.data?.size!!.toLong()))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse(false,"No dataset with id: $dataset_id found"))
        }
    }

}