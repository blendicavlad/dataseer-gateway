package com.application.app.config

import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.properties.Delegates

@ConfigurationProperties(prefix = "app")
open class AppProperties {

    val auth : Auth = Auth()

    val oauth2 : OAuth2 = OAuth2()

    open class Auth {
        lateinit var tokenSecret : String
        var tokenExpirationMsec by Delegates.notNull<Long>()
    }

    class OAuth2 {
        val authorizedRedirectUris : List<String> = ArrayList()
    }
}
