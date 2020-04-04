package com.dataseer.app.security.oauth2

import com.dataseer.app.exception.OAuth2AuthenticationProcessingException
import com.dataseer.app.model.AuthProvider
import com.dataseer.app.model.User
import com.dataseer.app.repository.UserRepository
import com.dataseer.app.security.UserPrincipal
import com.dataseer.app.security.oauth2.user.OAuth2UserInfo
import com.dataseer.app.security.oauth2.user.OAuth2UserInfoFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

/**
 * Retrieves the details of the authenticated user and creates a new entry in the database or updates the existing entry with the same email
 * @author Blendica Vlad
 * @date 05.03.2020
 */
@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    @Autowired lateinit var userRepository : UserRepository

    /**
     * Returns an OAuth2User after obtaining the user attributes of the End-User from the UserInfo Endpoint, then process it
     */
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User? {
        val oAuth2User = super.loadUser(oAuth2UserRequest)
        return try {
            processOAuth2User(oAuth2UserRequest, oAuth2User)
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) {
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    /**
     * Core Business Logic for OAuth2 Authentication
     * @param oAuth2UserRequest [OAuth2UserRequest]
     * @param oAuth2User [OAuth2User]
     */
    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User? {
        //Builds user info for each specific provider
        val oAuth2UserInfo: OAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.clientRegistration.registrationId, oAuth2User.attributes)
        if (StringUtils.isEmpty(oAuth2UserInfo.email)) {
            throw OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider")
        }
        var user: User? = oAuth2UserInfo.email?.let { userRepository.findByEmail(it) }

        //Checks if user exists and is signed in with another provider, if not, update his info from the OAuth response information, or create new user
        user = if (user != null) {
            if (!user.provider?.equals(AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId))!!) {
                throw OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.provider.toString() + " account. Please use your " + user.provider.toString() +
                        " account to login.")
            }
            updateExistingUser(user, oAuth2UserInfo)
        } else {
            registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }
        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    /**
     * Register new user by [OAuth2UserInfo]
     * @param oAuth2UserRequest [OAuth2UserRequest]
     * @param oAuth2UserInfo [OAuth2UserInfo]
     * @return [User]
     */
    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {
        val user = User()
        user.provider = AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)
        user.providerId = oAuth2UserInfo.id
        user.name = oAuth2UserInfo.name
        user.email = oAuth2UserInfo.email
        user.imageUrl = oAuth2UserInfo.imageUrl
        return userRepository.save(user)
    }

    /**
     * Updates existing user by [OAuth2UserInfo]
     * @param existingUser [User]
     * @param oAuth2UserInfo [OAuth2UserInfo]
     * @return [User]
     */
    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
        existingUser.name = oAuth2UserInfo.name
        existingUser.imageUrl = oAuth2UserInfo.imageUrl
        return userRepository.save(existingUser)
    }
}