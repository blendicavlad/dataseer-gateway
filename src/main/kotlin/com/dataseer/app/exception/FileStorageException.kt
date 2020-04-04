package com.dataseer.app.exception


/**
 * File storage problem [RuntimeException]
 * @author Blendica Vlad
 * @date 14.03.2020
 */
class FileStorageException : RuntimeException {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}