package com.application.app.payload

/**
 * Http response containing general info about the file uploaded
 * @author Blendica Vlad
 * @date 12.03.2020
 */
data class UploadFileResponse(
        val fileName: String,
        val fileDownloadUri: String?,
        val fileType: String,
        val size: Long
)