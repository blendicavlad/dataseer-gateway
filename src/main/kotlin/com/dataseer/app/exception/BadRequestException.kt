package com.dataseer.app.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Data structure of a wrong Http request [Exception]
 * @author Blendica Vlad
 * @date 02.03.2020
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}