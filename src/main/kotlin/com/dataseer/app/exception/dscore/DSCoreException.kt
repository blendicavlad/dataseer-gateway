package com.dataseer.app.exception.dscore

import java.lang.RuntimeException

class DSCoreException : RuntimeException {
    constructor(message: String) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}