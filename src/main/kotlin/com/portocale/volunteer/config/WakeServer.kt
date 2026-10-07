package com.portocale.volunteer.config

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import java.util.concurrent.TimeUnit

@Component
class WakeServer {

    private val logger = LoggerFactory.getLogger(WakeServer::class.java)
    private val renderUrl = System.getenv("RENDER_EXTERNAL_URL")

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    fun ping() {
        if (renderUrl.isNullOrBlank()) return

        try {
            URI("$renderUrl/actuator/health").toURL().openStream().close()
            logger.info("Server Pinged")
        } catch (e: IOException) {
            logger.warn("Ping failed: ${e.message}")
        }
    }
}