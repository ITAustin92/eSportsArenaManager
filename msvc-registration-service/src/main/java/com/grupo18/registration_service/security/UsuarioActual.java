package com.grupo18.registration_service.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

// Lee del JWT quién hace la petición. El claim "userId" es el id del perfil en msvc-user-service
// y lo agrega msvc-auth-service al firmar el token.
public final class UsuarioActual {

	private UsuarioActual() {}

	public static boolean esAdmin(Authentication autenticacion) {
		return (
			autenticacion != null &&
			autenticacion
				.getAuthorities()
				.stream()
				.anyMatch(permiso -> "ROLE_ADMIN".equals(permiso.getAuthority()))
		);
	}

	public static Long userId(Authentication autenticacion) {
		if (autenticacion != null && autenticacion.getPrincipal() instanceof Jwt jwt) {
			Object valor = jwt.getClaims().get("userId");
			if (valor instanceof Number numero) {
				return numero.longValue();
			}
			if (valor instanceof String texto) {
				try {
					return Long.valueOf(texto);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}
		return null;
	}

	public static Long exigirUserId(Authentication autenticacion) {
		Long id = userId(autenticacion);
		if (id == null) {
			throw new ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"Tu cuenta no tiene un perfil asociado"
			);
		}
		return id;
	}
}
