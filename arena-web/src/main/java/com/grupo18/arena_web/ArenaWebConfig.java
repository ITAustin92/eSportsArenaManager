package com.grupo18.arena_web;

import com.grupo18.registration_service.config.FeignAuthInterceptor;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import feign.RequestInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
class ArenaWebConfig {

	private final SecretKey claveJwt;

	ArenaWebConfig(@Value("${jwt.secret}") String secreto) {
		byte[] clave = secreto.getBytes(StandardCharsets.UTF_8);
		if (clave.length < 32) {
			throw new IllegalArgumentException("JWT_SECRETO debe tener al menos 32 bytes");
		}
		claveJwt = new SecretKeySpec(clave, "HmacSHA256");
	}

	@Bean
	JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<>(claveJwt));
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withSecretKey(claveJwt).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	RequestInterceptor feignAuthInterceptor() {
		return new FeignAuthInterceptor();
	}

	@Bean
	OpenAPI arenaOpenApi() {
		return new OpenAPI()
			.info(new Info().title("eSports Arena Manager").version("EP3"))
			.components(new Components().addSecuritySchemes(
				"bearer",
				new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
			));
	}

	@Bean
	UrlBasedCorsConfigurationSource corsConfigurationSource(
		@Value("${app.cors.origenes}") String origenes
	) {
		var cors = new CorsConfiguration();
		cors.setAllowedOrigins(Arrays.stream(origenes.split(",")).map(String::trim).toList());
		cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
		cors.setMaxAge(3600L);

		var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cors);
		return source;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		var roles = new JwtGrantedAuthoritiesConverter();
		roles.setAuthoritiesClaimName("roles");
		roles.setAuthorityPrefix("");

		var converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(roles);

		return http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/health", "/actuator/health/**", "/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/error")
				.permitAll()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
				.requestMatchers("/api/v1/auth/users/**").hasRole("ADMIN")
				.requestMatchers("/api/v1/auth/**").authenticated()
				.requestMatchers(HttpMethod.GET, rutas("games", "tournaments", "teams", "matches", "results",
					"rankings", "prizes", "registrations/tournament")).permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/users/public").permitAll()
				.requestMatchers(HttpMethod.GET, rutas("users", "registrations/team")).authenticated()
				.requestMatchers(HttpMethod.GET, "/api/v1/registrations/exists").authenticated()
				.requestMatchers(HttpMethod.GET, "/api/v1/registrations/mine").hasRole("JUGADOR")
				.requestMatchers(HttpMethod.GET, rutas("registrations")).hasAnyRole("ADMIN", "ORGANIZADOR")
				.requestMatchers(HttpMethod.GET, rutas("notifications", "sanctions"))
				.hasAnyRole("ADMIN", "ORGANIZADOR", "JUGADOR")
				.requestMatchers(HttpMethod.PUT, "/api/v1/users/*")
				.hasAnyRole("ADMIN", "ORGANIZADOR", "JUGADOR")
				.requestMatchers(HttpMethod.POST, "/api/v1/teams", "/api/v1/registrations")
				.hasAnyRole("ADMIN", "JUGADOR")
				.requestMatchers(HttpMethod.PUT, "/api/v1/teams/*").hasAnyRole("ADMIN", "JUGADOR")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/teams/*", "/api/v1/registrations/*")
				.hasAnyRole("ADMIN", "JUGADOR")
				.requestMatchers(rutas("games", "tournaments", "teams", "matches", "results", "rankings",
					"prizes", "registrations", "users", "notifications", "sanctions")).hasRole("ADMIN")
				.anyRequest().denyAll()
			)
			.build();
	}

	private static String[] rutas(String... areas) {
		return Arrays.stream(areas)
			.flatMap(area -> Arrays.stream(new String[] {"/api/v1/" + area + "/**", "/api/v2/" + area + "/**"}))
			.toArray(String[]::new);
	}
}
