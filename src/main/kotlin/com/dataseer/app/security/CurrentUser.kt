package com.dataseer.app.security

import org.springframework.security.core.annotation.AuthenticationPrincipal

/**
 * Annotation to be used to define the authenticated User Principal
 * @author Blendica Vlad
 * @date 02.03.2020
 */

@Target(AnnotationTarget.VALUE_PARAMETER,AnnotationTarget.TYPE)
@Retention(value = AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal
annotation class CurrentUser