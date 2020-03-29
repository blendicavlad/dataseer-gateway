package com.application.app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("email"))])
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var name: String? = null,

        @Email
        @Column(nullable = false)
        var email: String? = null,

        var imageUrl: String? = null,

        @Column(nullable = false)
        val emailVerified: Boolean = false,

        @JsonIgnore
        val password: String? = null,

        @Column(nullable = false, updatable = false)
        val creationDate: LocalDateTime = LocalDateTime.now(),

        @NotNull
        @Enumerated(EnumType.STRING) var provider: AuthProvider? = null,
        var providerId: String? = null
)
