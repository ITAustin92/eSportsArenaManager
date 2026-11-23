package com.grupo18.msvc_gateway.security;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Seguridad del GATEWAY: la PRIMERA barrera.
// 1) CORS: el frontend React (otro origen, puerto 5173) solo habla con el gateway, por eso CORS se
//    configura AQUÍ y en ningún microservicio (si no, el navegador vería cabeceras duplicadas).
// 2) JWT: revisa firma y vencimiento ANTES de enrutar. Las rutas públicas (login, registro y la
//    lectura del catálogo) pasan sin token; el resto exige un token válido o responde 401.
// 3) Cada microservicio vuelve a validar el token y el ROL (segunda barrera).
@Configuration
public class SecurityConfig {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${app.cors.origenes:http://localhost:5173,http://127.0.0.1:5173}")
	private String origenesPermitidos;

	@Bean
	public JwtDecoder jwtDecoder() {
		SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
		authorities.setAuthoritiesClaimName("roles");
		authorities.setAuthorityPrefix("");
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authorities);
		return converter;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration cors = new CorsConfiguration();
		cors.setAllowedOrigins(Arrays.stream(origenesPermitidos.split(",")).map(String::trim).toList());
		cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
		cors.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cors);
		return source;
	}

	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http,
		JwtAuthenticationConverter converter
	) throws Exception {
		http
			.cors(Customizer.withDefaults())
			// API sin estado: el token viaja en cada request (cabecera Authorization), no hay cookies. CSRF off.
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth ->
				auth
					.requestMatchers(HttpMethod.GET, "/api/v1/users/public")
					.permitAll()
					.requestMatchers("/actuator/health", "/actuator/health/**")
					.permitAll()
					// Preflight del navegador
					.requestMatchers(HttpMethod.OPTIONS, "/**")
					.permitAll()
					// Login, registro y renovación: auth-service decide (refresh exige token vigente).
					.requestMatchers("/api/v1/auth/**")
					.permitAll()
					// Swagger del gateway y los docs de cada servicio.
					.requestMatchers(
						"/docs/**",
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/api-docs/**",
						"/webjars/**",
						"/error"
					)
					.permitAll()
					// Catálogo público: cualquier visitante puede ver torneos, juegos, equipos y resultados.
					.requestMatchers(
						HttpMethod.GET,
						"/api/v1/games/**",
						"/api/v2/games/**",
						"/api/v1/tournaments/**",
						"/api/v2/tournaments/**",
						"/api/v1/teams/**",
						"/api/v2/teams/**",
						"/api/v1/matches/**",
						"/api/v2/matches/**",
						"/api/v1/results/**",
						"/api/v2/results/**",
						"/api/v1/rankings/**",
						"/api/v2/rankings/**",
						"/api/v1/prizes/**",
						"/api/v2/prizes/**",
						"/api/v1/registrations/tournament/**"
					)
					.permitAll()
					// Cualquier otra ruta exige un token válido (si no, 401 y no se enruta).
					.anyRequest()
					.authenticated()
			)
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
		return http.build();
	}
}
