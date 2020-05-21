package com.dataseer.app.exception.dscore

import java.lang.RuntimeException

object BadFileTypeException : RuntimeException() {
    override val message = "The file type is bad, "
}