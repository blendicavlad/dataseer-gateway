package com.dataseer.app.security.oauth2.user

import com.dataseer.app.exception.OAuth2AuthenticationProcessingException
import com.dataseer.app.model.AuthProvider

/**
 * Factory Class to generate the required user information for each individual third party provider
 * @author Blendica Vlad
 * @date 05.03.2020
 */
object OAuth2UserInfoFactory {

    /**
     * Build the OAuth2 User Information model by provider (now only supports google)
     * @param registrationId [String]
     * @param attributes [Map]
     */
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return if (registrationId.equals(AuthProvider.google.toString(), ignoreCase = true)) {
            GoogleOAuth2UserInfo(attributes)
        } else {
            throw OAuth2AuthenticationProcessingException("Sorry! Login with $registrationId is not supported yet.")
        }
    }
}