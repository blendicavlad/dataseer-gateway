package com.dataseer.app.security

import com.dataseer.app.model.User
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * Defines the User Principal of [User] to be stored in the Spring context by session
 * @author Blendica Vlad
 * @date 02.03.2020
 */
class UserPrincipal(val id: Long, val email: String, private val password: String, private val authorities: Collection<GrantedAuthority>) : OAuth2User, UserDetails {
    private var attributes: Map<String, Any>? = null

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes!!
    }

    fun setAttributes(attributes: Map<String, Any>?) {
        this.attributes = attributes
    }

    override fun getName(): String {
        return id.toString()
    }

    companion object {
        /**
         * Creates a standard principal with standard user authority
         * @param user [User]
         * @return [UserPrincipal]
         */
        fun create(user: User): UserPrincipal {
            val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER"))
            return UserPrincipal(
                    user.id!!,
                    user.email!!,
                    user.password!!,
                    authorities
            )
        }

        /**
         * Creates principal with specific athributes recieved by OAuth context
         * An OAuth 2.0 user is composed of one or more attributes, for example, first name, middle name, last name, email, phone number, address, etc.
         * Each user attribute has a "name" and "value"
         * @param user [User]
         * @return [UserPrincipal]
         */
        fun create(user: User, attributes: Map<String, Any>?): UserPrincipal {
            val userPrincipal = create(user)
            userPrincipal.setAttributes(attributes)
            return userPrincipal
        }

    }

}