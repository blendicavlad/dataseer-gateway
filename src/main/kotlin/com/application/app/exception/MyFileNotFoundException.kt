package com.application.app.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * File not found [RuntimeException]
 * @author Blendica Vlad
 * @date 14.03.2020
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class MyFileNotFoundException : RuntimeException {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}