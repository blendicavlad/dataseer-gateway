package com.application.app.security.oauth2.user

/**
 * Standard data structure of OAuth2 User Information (Might be used by Google, Github, Ocka etc.)
 * @author Blendica Vlad
 * @date 05.03.2020
 */
abstract class OAuth2UserInfo(var attributes: Map<String, Any>) {

    abstract val id: String?
    abstract val name: String?
    abstract val email: String?
    abstract val imageUrl: String?

}