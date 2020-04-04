package com.dataseer.app.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Http request filter, validates that every [HttpServletRequest] is authenticated
 * @author Blendica Vlad
 * @date 03.03.2020
 */
class TokenAuthenticationFilter : OncePerRequestFilter() {

    @Autowired private lateinit var tokenProvider: TokenProvider

    @Autowired private lateinit var customUserDetailsService: CustomUserDetailsService

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TokenAuthenticationFilter::class.java)
    }

    /**
     * Token validation filter for every [HttpServletRequest]
     * @param request [HttpServletRequest]
     * @param response [HttpServletResponse]
     * @param filterChain [FilterChain]
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            val jwt: String? = getJwtFromRequest(request)
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                val userId = tokenProvider.getUserIdFromToken(jwt)
                val userDetails = customUserDetailsService.loadUserById(userId!!)
                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
        }
        filterChain.doFilter(request, response)
    }

    /**
     * Gets JWT Token from [HttpServletResponse], must start with "Bearer "
     * @param request [HttpServletRequest]
     * @return [String]
     */
    private fun getJwtFromRequest(request: HttpServletRequest): String? {

        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7, bearerToken.length)
        } else null
    }
}