package com.application.app.security.oauth2

import com.application.app.util.deleteCookie
import com.application.app.util.deserialize
import com.application.app.util.getCookie
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class HttpCookieOAuth2AuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
        const val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"
        const val cookieExpireSeconds = 180
    }

    override fun loadAuthorizationRequest(request: HttpServletRequest?): OAuth2AuthorizationRequest? {
        return request?.let {
            getCookie(it, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                ?.let { cookie -> deserialize(cookie, OAuth2AuthorizationRequest::class.java) }
        }
    }

    override fun saveAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest?, request: HttpServletRequest, response: HttpServletResponse) {
        authorizationRequest?.let {
            deleteCookie(request,response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            deleteCookie(request,response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest?): OAuth2AuthorizationRequest? = this.loadAuthorizationRequest(request)

    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        deleteCookie(request,response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        deleteCookie(request,response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }
}