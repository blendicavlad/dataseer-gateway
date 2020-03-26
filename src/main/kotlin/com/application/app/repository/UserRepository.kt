package com.application.app.repository

import com.application.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email : String) : User?

    fun existsByEmail(email: String) : Boolean

}