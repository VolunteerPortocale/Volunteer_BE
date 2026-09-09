package com.example.demo.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    companion object {
        const val BASIC_AUTH = "basicAuth"
        const val BEARER_AUTH = "bearerAuth"
    }

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(Info().description("API documentation"))

            //define security schemes
            .components(
                Components()

                    //basic auth
                    .addSecuritySchemes(
                        BASIC_AUTH,
                        SecurityScheme()
                            .name(BASIC_AUTH)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic")
                            .description("HTTP basic authentication - username/password")
                    )

                    //bearer token
                    .addSecuritySchemes(
                        BEARER_AUTH,
                        SecurityScheme()
                            .name(BEARER_AUTH)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT Bearer token")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(BEARER_AUTH))
            .addSecurityItem(SecurityRequirement().addList(BASIC_AUTH))
    }
}