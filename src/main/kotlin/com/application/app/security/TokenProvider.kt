package com.application.app.security

import com.application.app.config.AppProperties
import io.jsonwebtoken.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

/**
 * JWT Token provider and handler
 * @author Blendica Vlad
 * @date 04.03.2020
 */
@Service
class TokenProvider(val appProperties: AppProperties) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TokenProvider::class.java)
    }

    /**
     * Creates JWT tolem for [Authentication]
     * @param authentication [Authentication]
     * @return jwt token [String]
     */
    fun createToken(authentication: Authentication) : String {
        val userPrincipal = authentication.principal as UserPrincipal

        val now = Date()
        val expiryDate = Date(now.time + appProperties.auth.tokenExpirationMsec)

        return Jwts.builder()
                .setSubject(userPrincipal.id.toString())
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.auth.tokenSecret)
                .compact()
    }

    /**
     * Gets User ID From token
     * @param token [String]
     * @return user id [Long]
     */
    fun getUserIdFromToken(token: String?): Long? {
        val claims = Jwts.parser()
                .setSigningKey(appProperties.auth.tokenSecret)
                .parseClaimsJws(token)
                .body
        return claims.subject.toLong()
    }

    /**
     * Validates JWT Token
     * @param authToken [String]
     * @return [Boolean]
     */
    fun validateToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(appProperties.auth.tokenSecret).parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }

}