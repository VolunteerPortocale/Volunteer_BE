package com.example.demo.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import jakarta.annotation.PostConstruct
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.properties.SwaggerUiConfigProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(
    private val swaggerUiProperties: SwaggerUiConfigProperties
) {

    companion object {
        const val BASIC_AUTH = "basicAuth"
        const val BEARER_AUTH = "bearerAuth"
    }

    @PostConstruct
    fun configureSwaggerUi() {
        swaggerUiProperties.deepLinking = true               // direct urls for each endpoint (#/users/getbyid)
        swaggerUiProperties.filter = "true"                  // real-time search/filter bar
        swaggerUiProperties.displayOperationId = true        // shows method name next to endpoint
        swaggerUiProperties.persistAuthorization = true      // remember login/tokens on page reload
        swaggerUiProperties.displayRequestDuration = true    // show response latency in ms
        swaggerUiProperties.docExpansion = "list"            // keep endpoints collapsed on load
        swaggerUiProperties.operationsSorter = "method"      // sort by http method (get, post, etc.)
        swaggerUiProperties.tagsSorter = "alpha"             // sort tags alphabetically
        swaggerUiProperties.tryItOutEnabled = true           // auto-expand "try it out"
    }

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Volunteerio API")
                    .description(
                        """
                        ### Backend Service
                        Documentation for Volunteerio REST endpoints.
                        - **Base Path:** `/api/v1`
                        - **Security:** Basic Auth or Bearer JWT
                        """.trimIndent()
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        BASIC_AUTH,
                        SecurityScheme()
                            .name(BASIC_AUTH)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic")
                            .description("HTTP basic authentication - username/password")
                    )
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
