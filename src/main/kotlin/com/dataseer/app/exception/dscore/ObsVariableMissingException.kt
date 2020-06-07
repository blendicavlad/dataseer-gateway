package com.dataseer.app.exception.dscore

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.BAD_REQUEST)
object ObsVariableMissingException : RuntimeException() {
    override val message = "The observed variable: 'y' is missing"
}