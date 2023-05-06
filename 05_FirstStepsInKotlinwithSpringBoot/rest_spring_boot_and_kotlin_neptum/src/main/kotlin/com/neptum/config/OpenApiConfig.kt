package com.neptum.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("RESTFUL API with Kotlin 1.7 and Spring Boot 3.0.5")
                    .version("v1")
                    .description("Some description about your API")
                    .termsOfService("https://google.com")
                    .license(
                        License().name("Apache 2.0")
                            .url("https://google.com")
                    )
            )
    }
}