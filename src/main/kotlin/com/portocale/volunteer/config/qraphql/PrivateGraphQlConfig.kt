package com.portocale.volunteer.config.qraphql

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer
import org.springframework.graphql.execution.DefaultExecutionGraphQlService
import org.springframework.graphql.execution.GraphQlSource
import org.springframework.graphql.server.WebGraphQlHandler
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.RouterFunctions
import org.springframework.web.servlet.function.ServerResponse

@Configuration
class PrivateGraphQlConfig(
    private val applicationContext: ApplicationContext
) {

    @Bean
    fun privateGraphQlSource(): GraphQlSource {
        val controllerConfigurer = AnnotatedControllerConfigurer()

        controllerConfigurer.setControllerPredicate { controller ->
            controller.packageName.startsWith(
                "com.portocale.volunteer"
            )
        }

        controllerConfigurer.setApplicationContext(applicationContext)
        controllerConfigurer.afterPropertiesSet()

        val resources = PathMatchingResourcePatternResolver()
            .getResources("classpath*:graphql/private/**/*.graphqls")

        return GraphQlSource
            .schemaResourceBuilder()
            .schemaResources(*resources)
            .configureRuntimeWiring(controllerConfigurer)
            .build()
    }

    @Bean
    fun privateGraphQlRouter(
        privateGraphQlSource: GraphQlSource
    ): RouterFunction<ServerResponse> {

        val executionService =
            DefaultExecutionGraphQlService(privateGraphQlSource)

        val webHandler =
            WebGraphQlHandler
                .builder(executionService)
                .build()

        val httpHandler =
            GraphQlHttpHandler(webHandler)

        return RouterFunctions
            .route()
            .POST("/graphql", httpHandler::handleRequest)
            .build()
    }
}
