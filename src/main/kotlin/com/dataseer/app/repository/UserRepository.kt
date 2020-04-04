package com.dataseer.app.repository

import com.dataseer.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Defines the [User] repository
 * @author Blendica Vlad
 * @date 02.03.2020
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    /**
     * Get User by Email
     * @param email [String]
     * @return [User]
     */
    fun findByEmail(email : String) : User?

    /**
     * Checks if User exists, by Email
     * @param email [String]
     * @return [User]
     */
    fun existsByEmail(email: String) : Boolean

}