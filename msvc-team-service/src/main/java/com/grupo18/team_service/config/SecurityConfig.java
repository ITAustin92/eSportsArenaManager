package com.grupo18.team_service.config;
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
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
// Resource Server: valida el JWT en cada petición (segunda barrera de seguridad).
@Configuration
public class SecurityConfig {
    @Value("${jwt.secret}") private String secret;
    @Bean public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }
    @Bean public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter a = new JwtGrantedAuthoritiesConverter();
        a.setAuthoritiesClaimName("roles"); a.setAuthorityPrefix("");
        JwtAuthenticationConverter c = new JwtAuthenticationConverter();
        c.setJwtGrantedAuthoritiesConverter(a); return c;
    }
    @Bean public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter conv) throws Exception {
        http.csrf(c -> c.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/docs/**","/swagger-ui/**","/v3/api-docs/**","/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/teams/**","/api/v2/teams/**").hasAnyRole("ADMIN","ORGANIZADOR","JUGADOR")
                .requestMatchers("/api/v1/teams/**","/api/v2/teams/**").hasAnyRole("ADMIN","ORGANIZADOR")
                .anyRequest().authenticated())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(conv)))
            .headers(h -> h.frameOptions(f -> f.disable()));
        return http.build();
    }
}
