package com.application.app.security.oauth2

import com.application.app.config.AppProperties
import com.application.app.exception.BadRequestException
import com.application.app.security.TokenProvider
import com.application.app.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.Companion.REDIRECT_URI_PARAM_COOKIE_NAME
import com.application.app.util.getCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import java.net.URI
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * OAuth authentication succes handling business logic
 * @author Blendica Vlad
 * @date 06.03.2020
 */
@Component
class OAuth2AuthenticationSuccessHandler(
        private val tokenProvider: TokenProvider,
        private val appProperties: AppProperties,
        private val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository) : SimpleUrlAuthenticationSuccessHandler() {


    /**
     * Succes auth event listener
     * @param request [HttpServletRequest]
     * @param response [HttpServletResponse
     * @param authentication [Authentication]
     */
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val targetUrl = determineTargetUrl(request, response, authentication)
        if (response.isCommitted) {
            logger.debug("Response has already been committed. Unable to redirect to $targetUrl")
            return
        }
        clearAuthenticationAttributes(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }


    /**
     * Validades redirect url and create JWT token
     * @param request [HttpServletRequest]
     * @param response [HttpServletResponse]
     * @param authentication [Authentication]
     * @return token [String]
     */
    override fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication): String? {

        val redirectUri : String? = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).let{ it?.value }

        if(redirectUri != null && !isAuthorizedRedirectUri(redirectUri))
            throw BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication")

        val targetUrl = redirectUri ?: defaultTargetUrl

        val token = authentication.let { tokenProvider.createToken(it) }

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString()
    }

    /**
     * As the method name says
     * @param request [HttpServletRequest]
     * @param response [HttpServletResponse]
     */
    protected fun clearAuthenticationAttributes(request: HttpServletRequest, response: HttpServletResponse) {
        super.clearAuthenticationAttributes(request)
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

    /**
     * Verifies if the redirect uri matches one of the uri-s defined in app properties
     * @param uri [String]
     * @return [Boolean]
     */
    private fun isAuthorizedRedirectUri(uri : String) : Boolean {
        val clientRedirectUri : URI = URI.create(uri)

        return appProperties.oauth2.authorizedRedirectUris
                .any {
                    val authorizedUri : URI = URI.create(it)
                    if (authorizedUri.host.equals(clientRedirectUri.host, ignoreCase = true) && authorizedUri.port == clientRedirectUri.port)
                       return true
                    return false
                }

    }
}