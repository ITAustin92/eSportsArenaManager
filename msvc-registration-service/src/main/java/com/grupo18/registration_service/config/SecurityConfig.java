package com.grupo18.registration_service.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// Resource Server: valida el JWT en cada petición y el ROL de cada ruta (segunda barrera de seguridad).
// Los roles vienen en el claim "roles" del token (ROLE_ADMIN, ROLE_ORGANIZADOR, ROLE_JUGADOR).
@Configuration
public class SecurityConfig {

	@Value("${jwt.secret}")
	private String secret;

	@Bean
	public JwtDecoder jwtDecoder() {
		SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter a = new JwtGrantedAuthoritiesConverter();
		a.setAuthoritiesClaimName("roles");
		a.setAuthorityPrefix("");
		JwtAuthenticationConverter c = new JwtAuthenticationConverter();
		c.setJwtGrantedAuthoritiesConverter(a);
		return c;
	}

	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http,
		JwtAuthenticationConverter conv
	) throws Exception {
		http
			.csrf(c -> c.disable())
			.authorizeHttpRequests(auth ->
				auth
					.requestMatchers("/actuator/health", "/actuator/health/**")
					.permitAll()
					.requestMatchers(HttpMethod.OPTIONS, "/**")
					.permitAll()
					.requestMatchers(
						"/docs/**",
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/h2-console/**",
						"/error"
					)
					.permitAll()
					// Inscritos por torneo: público (cupos y participantes del torneo)
					.requestMatchers(
						HttpMethod.GET,
						"/api/v1/registrations/tournament/**",
						"/api/v2/registrations/tournament/**"
					)
					.permitAll()
					.requestMatchers(HttpMethod.GET, "/api/v1/registrations/mine")
					.hasRole("JUGADOR")
					// Inscripciones de un equipo y verificación: cualquier sesión válida
					.requestMatchers(
						HttpMethod.GET,
						"/api/v1/registrations/team/**",
						"/api/v2/registrations/team/**",
						"/api/v1/registrations/exists"
					)
					.authenticated()
					// Listado y detalle de inscripciones (las "órdenes"): ADMIN y ORGANIZADOR (solo lectura)
					.requestMatchers(
						HttpMethod.GET,
						"/api/v1/registrations/**",
						"/api/v2/registrations/**",
						"/api/v1/registrations",
						"/api/v2/registrations"
					)
					.hasAnyRole("ADMIN", "ORGANIZADOR")
					// Inscribir y cancelar: el JUGADOR solo con equipos donde es capitán (lo valida el controlador)
					.requestMatchers(HttpMethod.POST, "/api/v1/registrations")
					.hasAnyRole("ADMIN", "JUGADOR")
					.requestMatchers(HttpMethod.DELETE, "/api/v1/registrations/*")
					.hasAnyRole("ADMIN", "JUGADOR")
					// Cambiar estado (confirmar/rechazar) y escrituras v2: solo ADMIN
					.requestMatchers("/api/v1/registrations/**", "/api/v2/registrations/**")
					.hasRole("ADMIN")
					.anyRequest()
					.authenticated()
			)
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(conv)))
			.headers(h -> h.frameOptions(f -> f.disable()));
		return http.build();
	}
}
