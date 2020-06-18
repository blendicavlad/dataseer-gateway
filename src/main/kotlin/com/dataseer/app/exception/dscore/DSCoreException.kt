package com.dataseer.app.exception.dscore

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.BAD_REQUEST)
class DSCoreException : RuntimeException {
    constructor(message: String) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}