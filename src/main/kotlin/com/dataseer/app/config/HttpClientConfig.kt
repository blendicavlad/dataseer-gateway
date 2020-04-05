package com.dataseer.app.config

import com.dataseer.app.util.BeanProvider
import org.apache.http.HeaderElement
import org.apache.http.HeaderElementIterator
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeaderElementIterator
import org.apache.http.protocol.HTTP
import org.apache.http.protocol.HttpContext
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit

/**
 * Supports both HTTP and HTTPS
 * Uses a connection pool to re-use connections and save overhead of creating connections.
 * Has a custom connection keep-alive strategy (to apply a default keep-alive if one isn't specified)
 * Starts an idle connection monitor to continuously clean up stale connections.
 * @author Blendica Vlad
 * @date 04.05.2020
 */
@Configuration
class HttpClientConfig {
    @Bean
    fun poolingConnectionManager(): PoolingHttpClientConnectionManager {
        val builder = SSLContextBuilder()
        try {
            builder.loadTrustMaterial(null, TrustSelfSignedStrategy())
        } catch (e: NoSuchAlgorithmException) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        } catch (e: KeyStoreException) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        }
        var sslsf: SSLConnectionSocketFactory? = null
        try {
            sslsf = SSLConnectionSocketFactory(builder.build())
        } catch (e: KeyManagementException) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        } catch (e: NoSuchAlgorithmException) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        }
        val socketFactoryRegistry: Registry<ConnectionSocketFactory> = RegistryBuilder.create<ConnectionSocketFactory>()
                .register("https",sslsf).register("http",PlainConnectionSocketFactory())
                .build()
        val poolingConnectionManager = PoolingHttpClientConnectionManager(socketFactoryRegistry)
        poolingConnectionManager.maxTotal = MAX_TOTAL_CONNECTIONS
        return poolingConnectionManager
    }

    @Bean
    fun connectionKeepAliveStrategy(): ConnectionKeepAliveStrategy {
        return object : ConnectionKeepAliveStrategy {
            override fun getKeepAliveDuration(response: HttpResponse, context: HttpContext?): Long {
                val it: HeaderElementIterator = BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE))
                while (it.hasNext()) {
                    val he: HeaderElement = it.nextElement()
                    val param: String = he.name
                    val value: String = he.value
                    if (param.equals("timeout", ignoreCase = true)) {
                        return value.toLong() * 1000
                    }
                }
                return DEFAULT_KEEP_ALIVE_TIME_MILLIS.toLong()
            }
        }
    }

    @Bean
    fun httpClient(): CloseableHttpClient {
        val requestConfig: RequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build()
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build()
    }

    /**
     * Periodically check all connections and free up which have not been used and idle time has elapsed
     */
    @Scheduled(fixedDelay = CONNECT_TIMEOUT.toLong())
    fun idleConnectionMonitor() {
        val connectionManager : PoolingHttpClientConnectionManager = BeanProvider.getBean(PoolingHttpClientConnectionManager::class.java)
        try {
            log.trace("run IdleConnectionMonitor - Closing expired and idle connections...")
            connectionManager.closeExpiredConnections()
            connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS.toLong(), TimeUnit.SECONDS)
        } catch (e: Exception) {
            log.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.message, e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(HttpClientConfig::class.java)

        // Determines the timeout in milliseconds until a connection is established.
        private const val CONNECT_TIMEOUT = 30000

        // The timeout when requesting a connection from the connection manager.
        private const val REQUEST_TIMEOUT = 30000

        // The timeout for waiting for data
        private const val SOCKET_TIMEOUT = 60000
        private const val MAX_TOTAL_CONNECTIONS = 50
        private const val DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000
        private const val CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30
    }
}