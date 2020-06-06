package com.dataseer.app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

/**
 * User Model
 * @author Blendica Vlad
 * @date 01.03.2020
 */
@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("email"))])
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false, length = 60)
        var name: String,

        @Email
        @Column(nullable = false, length = 60)
        var email: String,

        var imageUrl: String? = null,

        @Column(nullable = false)
        val emailVerified: Boolean = false,

        @JsonIgnore
        val password: String,

        @Column(nullable = false, updatable = false)
        val creationDate: LocalDateTime = LocalDateTime.now(),

        @NotNull
        @Enumerated(EnumType.STRING) var provider: AuthProvider? = null,
        var providerId: String? = null
)
