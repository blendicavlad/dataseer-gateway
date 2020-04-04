package com.application.app.security.oauth2

import com.application.app.util.getCookie
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * OAuth authentication failure handling business logic
 * @author Blendica Vlad
 * @date 06.03.2020
 */
@Component
class OAuth2AuthenticationFailureHandler(private val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository) : SimpleUrlAuthenticationFailureHandler() {

    /**
     * Failure auth event listener
     * @param request [HttpServletRequest]
     * @param response [HttpServletResponse
     * @param exception [AuthenticationException]
     */
    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException?) {

        var targetUrl : String = getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME).let{ it!!.value }
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error",exception?.localizedMessage)
                .build().toUriString()

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request,response)

        redirectStrategy.sendRedirect(request,response,targetUrl)
    }
}