package com.grupo18.auth_service.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${jwt.secret}")
	private String secret;

	private SecretKey claveHmac() {
		return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<>(claveHmac()));
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withSecretKey(claveHmac()).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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
		JwtAuthenticationConverter converter
	) throws Exception {
		http
			.csrf(c -> c.disable())
			.authorizeHttpRequests(auth ->
				auth
					.requestMatchers("/actuator/health", "/actuator/health/**")
					.permitAll()
					.requestMatchers(HttpMethod.OPTIONS, "/**")
					.permitAll()
					// Públicos: todavía no hay token
					.requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register")
					.permitAll()
					.requestMatchers(
						"/docs/**",
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/h2-console/**",
						"/error"
					)
					.permitAll()
					// Gestión de cuentas: solo administradores
					.requestMatchers("/api/v1/auth/users/**", "/api/v1/auth/users")
					.hasRole("ADMIN")
					// /refresh y /me: cualquier sesión válida
					.anyRequest()
					.authenticated()
			)
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter)))
			.headers(h -> h.frameOptions(f -> f.disable()));
		return http.build();
	}
}
