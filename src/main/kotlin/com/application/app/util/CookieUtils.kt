package com.application.app.util

import org.springframework.util.SerializationUtils
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Cookie helper utility functions
 * @author Blendica Vlad
 * @date 06.03.2020
 */

/**
 * Gets a cookie by name from a [HttpServletRequest]
 * @param request [HttpServletRequest]
 * @param name [String]
 * @return [Cookie]
 */
fun getCookie(request: HttpServletRequest, name : String) : Cookie? {

    val cookies = request.cookies

    if (cookies != null && cookies.isNotEmpty()) {
        for (cookie in cookies) {
            if (cookie.name == name)
                return cookie
        }
    }
    return null
}

/**
 * Adds a cookie to a [HttpServletResponse]
 * @param response [HttpServletResponse]
 * @param name [String]
 * @param value [String]
 * @param maxAge [Int]
 */
fun addCookie(response: HttpServletResponse, name: String, value : String, maxAge : Int) {

    val cookie = Cookie(name,value)
    cookie.path = "/"
    cookie.isHttpOnly = true
    cookie.maxAge = maxAge
    response.addCookie(cookie)
}

/**
 * Deletes cookie from request and adds it to the response
 * @param request [HttpServletRequest]
 * @param response [HttpServletResponse]
 * @param name [String]
 */
fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {

    val cookies = request.cookies

    if (cookies != null && cookies.isNotEmpty()) {
        for (cookie in cookies) {
            if (cookie.name == name) {
                cookie.value = ""
                cookie.path = "/"
                cookie.maxAge = 0
                response.addCookie(cookie)
            }
        }
    }
}

/**
 * Serialize Object to Base64
 * @param obj [Any]
 * @return serialized object [String]
 */
fun serialize(obj : Any) : String = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj))

/**
 * Base64 deserialize Object from Cookie
 * @param cookie [Cookie]
 * @param cls [Class]
 * @return The deserialized object [T]
 */
fun <T> deserialize(cookie: Cookie, cls : Class<T>) : T = cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.value)))