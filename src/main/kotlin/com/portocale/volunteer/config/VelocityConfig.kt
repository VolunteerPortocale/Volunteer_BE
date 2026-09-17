package com.portocale.volunteer.config

import org.apache.velocity.app.VelocityEngine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Properties

@Configuration
class VelocityConfig {

    @Bean
    fun velocityEngine(): VelocityEngine {
        val properties = Properties().apply {
            setProperty("resource.loaders", "class")
            setProperty(
                "resource.loader.class.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
            )
            setProperty(
                "eventhandler.include.class",
                "org.apache.velocity.app.event.implement.IncludeRelativePath"
            )
            setProperty("input.encoding", "UTF-8")
            setProperty("output.encoding", "UTF-8")
        }
        return VelocityEngine(properties).apply { init() }
    }
}
