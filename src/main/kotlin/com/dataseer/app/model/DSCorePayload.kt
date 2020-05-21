package com.dataseer.app.model

data class DSCorePayload(val payload: DataFrame) {
    data class DataFrame(val data_frame: String? = null)
}