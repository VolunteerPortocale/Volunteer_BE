package com.portocale.volunteer

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.context.WebServerApplicationContext
import org.springframework.core.env.getProperty

@SpringBootApplication(scanBasePackages = ["com.portocale"])
class Application

private val log = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    val context = runApplication<Application>(*args)

    val environment = context.environment

    val activeProfiles = environment.activeProfiles
        .takeIf { it.isNotEmpty() }
        ?.joinToString(", ")
        ?: environment.defaultProfiles.joinToString(", ")

    val port = (context as? WebServerApplicationContext)
        ?.webServer
        ?.port
        ?: environment.getProperty("server.port", "8080").toInt()

    val protocol = if (environment.getProperty<Boolean>("server.ssl.enabled", false)) "https" else "http"

    log.info(
        """
        
        ----------------------------------------------------------
        Application started successfully
        Active profiles: {}
        URL: {}://localhost:{}
        ----------------------------------------------------------
        """.trimIndent(),
        activeProfiles,
        protocol,
        port
    )
}
