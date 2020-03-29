package com.application.app.payload

data class UploadFileResponse(
        val fileName: String,
        val fileDownloadUri: String?,
        val fileType: String,
        val size: Long
)