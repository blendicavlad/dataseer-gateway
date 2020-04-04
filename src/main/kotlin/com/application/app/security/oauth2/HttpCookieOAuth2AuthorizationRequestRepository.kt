package com.application.app.security.oauth2

import com.application.app.util.*
import com.nimbusds.oauth2.sdk.util.StringUtils
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Userd to persist OAuth2 authorisation reuqsts to be cached, resolved, filtered etc.
 * @author Blendica Vlad
 * @date 06.03.2020
 */
@Component
class HttpCookieOAuth2AuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
        const val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"
        const val cookieExpireSeconds = 180
    }

    /**
     * Deserialize Oauth cookie from request into [OAuth2AuthorizationRequest], maybe prone to security vulnerabilities but fuck it
     * @param request [HttpServletRequest]
     * @return OAuth auth requeest object [OAuth2AuthorizationRequest]
     */
    override fun loadAuthorizationRequest(request: HttpServletRequest?): OAuth2AuthorizationRequest? {
        return request?.let {
            getCookie(it, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                ?.let { cookie -> deserialize(cookie, OAuth2AuthorizationRequest::class.java) }
        }
    }

    /**
     * Persists the [OAuth2AuthorizationRequest] in a cookie, associating it to the provided [HttpServletRequest]
     */
    override fun saveAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest?, request: HttpServletRequest, response: HttpServletResponse) {
        authorizationRequest?.let {
            deleteCookie(request,response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            deleteCookie(request,response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }
        addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serialize(authorizationRequest!!), cookieExpireSeconds)
        val redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME)
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds)
        }
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest?): OAuth2AuthorizationRequest? = this.loadAuthorizationRequest(request)

    /**
     * Cleans OAuth2 cookies
     * @param request [HttpServletRequest]
     * @param response [HttpServletResponse]
     */
    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        deleteCookie(request,response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        deleteCookie(request,response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }
}