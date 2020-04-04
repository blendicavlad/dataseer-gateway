package com.dataseer.app.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Http auth exception handling interceptor
 * @author Blendica Vlad
 * @date 03.03.2020
 */
class RestAuthenticationEntryPoint : AuthenticationEntryPoint {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint::class.java)
    }

    /**
     * Create unauthorized response in case of bad credentials
     * @param httpServletRequest [HttpServletRequest]
     * @param httpServletResponse [HttpServletRequest]
     * @param e [AuthenticationException]
     */
    @Throws(IOException::class, ServletException::class)
    override fun commence(httpServletRequest: HttpServletRequest?, httpServletResponse: HttpServletResponse, e: AuthenticationException) {

        logger.error("Responding with unauthorized error. Message - {}", e.message)
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.localizedMessage)
    }
}