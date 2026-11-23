package com.grupo18.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI()
			// Botón "Authorize" en Swagger: pegar el token JWT obtenido en POST /api/v1/auth/login
			.components(
				new io.swagger.v3.oas.models.Components().addSecuritySchemes(
					"bearer",
					new io.swagger.v3.oas.models.security.SecurityScheme()
						.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
				)
			)
			.addSecurityItem(
				new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearer")
			)
			.info(
				new Info()
					.title("API Users")
					.version("1.0")
					.description("API Users - eSports Arena Manager")
			);
	}
}
