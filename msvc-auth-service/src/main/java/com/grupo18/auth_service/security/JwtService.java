package com.grupo18.auth_service.security;

import com.grupo18.auth_service.models.Rol;
import com.grupo18.auth_service.models.Usuario;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

// Genera (FIRMA) el JWT que el resto del sistema validará con la misma clave (HS256).
// Claims: sub (username), roles, rol principal, nombre, userId (id del perfil en user-service), iat y exp.
@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;

	@Value("${jwt.expiration-minutes:60}")
	private long expirationMinutes;

	public JwtService(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	public Jwt generarToken(Usuario usuario) {
		Instant ahora = Instant.now();
		List<String> roles = usuario.getRoles().stream().map(Rol::getNombre).sorted().toList();
		JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
			.issuer("msvc-auth-service")
			.issuedAt(ahora)
			.expiresAt(ahora.plus(expirationMinutes, ChronoUnit.MINUTES))
			.subject(usuario.getUsername())
			.claim("roles", roles)
			.claim("rol", rolPrincipal(roles))
			.claim("nombre", usuario.getNombre() == null ? usuario.getUsername() : usuario.getNombre());
		if (usuario.getPerfilId() != null) {
			claims.claim("userId", usuario.getPerfilId());
		}
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build()));
	}

	// Token de 1 minuto para llamadas servicio-a-servicio (por ejemplo, crear el perfil en user-service).
	public String generarTokenServicio() {
		Instant ahora = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuer("msvc-auth-service")
			.issuedAt(ahora)
			.expiresAt(ahora.plus(1, ChronoUnit.MINUTES))
			.subject("msvc-auth-service")
			.claim("roles", List.of("ROLE_ADMIN"))
			.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	// Si una cuenta tuviera varios roles, el de más privilegio define su vista en el frontend.
	public static String rolPrincipal(List<String> roles) {
		if (roles.contains("ROLE_ADMIN")) return "ROLE_ADMIN";
		if (roles.contains("ROLE_ORGANIZADOR")) return "ROLE_ORGANIZADOR";
		return "ROLE_JUGADOR";
	}
}
