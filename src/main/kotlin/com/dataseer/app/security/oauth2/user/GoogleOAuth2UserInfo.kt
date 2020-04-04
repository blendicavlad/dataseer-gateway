package com.dataseer.app.security.oauth2.user

/**
 * Defines the User information of standard Google OAuth2 Flow
 * @author Blendica Vlad
 * @date 05.03.2020
 */
class GoogleOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String?
        get() = attributes["sub"] as String?

    override val name: String?
        get() = attributes["name"] as String?

    override val email: String?
        get() = attributes["email"] as String?

    override val imageUrl: String?
        get() = attributes["picture"] as String?
}