package com.application.app.model

import net.minidev.json.annotate.JsonIgnore
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

        @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL])
        var userData: UserData? = null,

        @JsonIgnore
        val password: String? = null,

        @NotNull
        @Enumerated(EnumType.STRING) var provider: AuthProvider? = null,
        var providerId: String? = null
)
