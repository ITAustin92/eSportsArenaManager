package com.grupo18.arena_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
	basePackages = "com.grupo18",
	nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.REGEX,
		pattern = "com\\.grupo18\\.[a-z]+_service\\..*(Application|SecurityConfig|SwaggerConfig|FeignAuthInterceptor)"
	)
)
@EntityScan("com.grupo18")
@EnableJpaRepositories("com.grupo18")
@EnableFeignClients("com.grupo18")
public class ArenaWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArenaWebApplication.class, args);
	}
}
