package com.application.app.exception

import org.springframework.security.core.AuthenticationException

/**
 * Error in processing OAuth authentication
 * @author Blendica Vlad
 * @date 05.03.2020
 */
class OAuth2AuthenticationProcessingException : AuthenticationException {
    constructor(msg: String?, t: Throwable?) : super(msg, t)
    constructor(msg: String?) : super(msg)
}