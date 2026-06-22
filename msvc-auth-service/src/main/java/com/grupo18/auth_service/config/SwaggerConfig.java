package com.grupo18.auth_service.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {
    @Bean public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API Auth - eSports Arena Manager")
                .version("1.0")
                .description("Registro, login y emisión de JWT. Roles: ROLE_ADMIN, ROLE_ORGANIZADOR, ROLE_JUGADOR"));
    }
}
