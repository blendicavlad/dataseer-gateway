package com.application.app.util

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext

import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service

@Service
class BeanProvider : ApplicationContextAware {

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    companion object {
        private lateinit var context: ApplicationContext

        fun <T> getBean(beanClass: Class<T>): T {
            return context.getBean(beanClass)
        }
    }
}