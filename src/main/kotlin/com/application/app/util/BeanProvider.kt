package com.application.app.util

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext

import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service

/**
 * Utility class wired into the Spring context so can extract wired beans and use them outside Spring scope
 * Hacky? Maybe
 * @author Blendica Vlad
 * @date 14.03.2020
 */
@Service
class BeanProvider : ApplicationContextAware {

    /**
     * Statically link the application context
     * @param applicationContext [ApplicationContext]
     */
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    companion object {
        private lateinit var context: ApplicationContext

        /**
         * Provides wired Spring beans out of internal context, so we can manually wire them
         */
        fun <T> getBean(beanClass: Class<T>): T {
            return context.getBean(beanClass)
        }
    }
}