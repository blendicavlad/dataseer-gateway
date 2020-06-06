package com.dataseer.app.config

import com.dataseer.app.security.CustomUserDetailsService
import com.dataseer.app.security.RestAuthenticationEntryPoint
import com.dataseer.app.security.TokenAuthenticationFilter
import com.dataseer.app.security.oauth2.CustomOAuth2UserService
import com.dataseer.app.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.dataseer.app.security.oauth2.OAuth2AuthenticationFailureHandler
import com.dataseer.app.security.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Security configurations
 * @author Blendica Vlad
 * @date 01.03.2020
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired private lateinit var customUserDetailsService : CustomUserDetailsService

    @Autowired private lateinit var customOAuth2UserService: CustomOAuth2UserService

    @Autowired private lateinit var oAuth2AuthenticationSuccessHandler : OAuth2AuthenticationSuccessHandler

    @Autowired private lateinit var oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler

    @Autowired private lateinit var httpCookieOAuth2AuthorizationRequestRepository : HttpCookieOAuth2AuthorizationRequestRepository

    @Bean fun tokenAuthenticationFilter() : TokenAuthenticationFilter = TokenAuthenticationFilter()

    @Bean fun cookieAuthorizationRequestRepository() : HttpCookieOAuth2AuthorizationRequestRepository = HttpCookieOAuth2AuthorizationRequestRepository()

    /**
     * @param auth
     * Injects custom user details service and password encoder
     * into the authentication manager
     */
    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth
            ?.userDetailsService(customUserDetailsService)
            ?.passwordEncoder(passwordEncoder())

    }

    @Bean fun passwordEncoder() : BCryptPasswordEncoder = BCryptPasswordEncoder()

    /**
     * @return AuthenticationManager
     * Registers the authentication manager into the bean context
     */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    /**
     * @param http
     * Configures general security rules (CORS/Session Management/Authorised endpoints etc.)
     */
    override fun configure(http: HttpSecurity) {
        http
            .cors()
            .and()
        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
        .csrf()
            .disable()
        .formLogin()
            .disable()
        .httpBasic()
            .disable()
        .exceptionHandling()
            .authenticationEntryPoint(RestAuthenticationEntryPoint())
            .and()
        .authorizeRequests()
            .antMatchers("/",
                    "/error",
                    "/favicon.ico",
                    "/**/*.png",
                    "/**/*.gif",
                    "/**/*.svg",
                    "/**/*.jpg",
                    "/**/*.html",
                    "/**/*.css",
                    "/**/*.js")
                .permitAll()
            .antMatchers("/auth/**", "/oauth2/**")
                .permitAll()
            .anyRequest()
                .authenticated()
            .and()
        .oauth2Login()
            .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
            .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
            .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler);

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}