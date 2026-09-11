package com.example.demo.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
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
            .servers(
                listOf(
                    Server().url("http://localhost:8081").description("Local Development"),
                    Server().url("https://volunteer-be-rs60.onrender.com/").description("Render Server")
                )
            )
            .tags(
                listOf(
                    Tag().name("Users").description("Operations related to user accounts and profiles")
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

    @Bean
    fun openApiCustomizer(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->

            openApi.paths["/api/v1/users"]?.get?.apply {
                tags = listOf("Users")
                summary = "Retrieve all users"
                description = "Fetches a full list of all registered users in the database."
                responses.addApiResponse("200", ApiResponse().description("Users retrieved successfully"))
                responses.addApiResponse("401", ApiResponse().description("Unauthorized access"))
            }

            openApi.paths["/api/v1/users/{id}"]?.get?.apply {
                tags = listOf("Users")
                summary = "Get user by ID"
                description = "Finds a single user profile using their MongoDB ObjectId."
                parameters?.find { it.name == "id" }?.apply {
                    description = "Hexadecimal string representing the user ID"
                    example = "64f1a2b3c4d5e6f7a8b9c0d1"
                }

                //to change after exception handling implemented
                responses.addApiResponse("200", ApiResponse().description("User found"))
                responses.addApiResponse("404", ApiResponse().description("User not found"))
                responses.addApiResponse("401", ApiResponse().description("Unauthorized access"))
            }

            openApi.components?.schemas?.get("User")?.apply {
                description = "Data transfer object representing a user"
                properties?.get("id")?.apply {
                    description = "Unique MongoDB ObjectId"
                    example = "64f1a2b3c4d5e6f7a8b9c0d1"
                }
                properties?.get("name")?.apply {
                    description = "User's full name"
                    example = "Kebab Nurmagomedov"
                }
                properties?.get("age")?.apply {
                    description = "User's age in years"
                    example = 28
                }
            }
        }
    }
}