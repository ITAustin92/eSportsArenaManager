package com.grupo18.game_service.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {
    @Bean public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title("API Games").version("1.0").description("API Games - eSports Arena Manager"));
    }
}
