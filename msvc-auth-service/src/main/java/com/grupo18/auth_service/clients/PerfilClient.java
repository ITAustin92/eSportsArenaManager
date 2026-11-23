package com.grupo18.auth_service.clients;

import com.grupo18.auth_service.security.JwtService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

// Crea el perfil del usuario en msvc-user-service cuando se registra una cuenta.
// Como el POST de usuarios exige ROLE_ADMIN, auth-service se identifica con un
// token de servicio de corta duración que él mismo firma.
@Component
public class PerfilClient {

	private final RestClient restClient;
	private final JwtService jwtService;

	public PerfilClient(
		@Value("${servicios.user-service-url:http://localhost:8000}") String urlBase,
		JwtService jwtService
	) {
		this.restClient = RestClient.builder().baseUrl(urlBase).build();
		this.jwtService = jwtService;
	}

	public void exigirActivo(Long id) {
		try {
			Map<?, ?> perfil = restClient
				.get()
				.uri("/api/v1/users/{id}", id)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generarTokenServicio())
				.retrieve()
				.body(Map.class);
			if (perfil == null || "INACTIVO".equals(perfil.get("estado"))) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta está desactivada");
			}
		} catch (RestClientException e) {
			throw new ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE,
				"No se pudo verificar el estado de la cuenta"
			);
		}
	}

	public void desactivar(Long id) {
		try {
			restClient
				.delete()
				.uri("/api/v1/users/{id}", id)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generarTokenServicio())
				.retrieve()
				.toBodilessEntity();
		} catch (RestClientException e) {
			throw new ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE,
				"No se pudo desactivar el perfil asociado"
			);
		}
	}

	public Long crearPerfil(String nombre, String nickname, String correo, String rolPerfil) {
		Map<String, Object> cuerpo = new LinkedHashMap<>();
		cuerpo.put("nombre", nombre);
		cuerpo.put("nickname", nickname);
		cuerpo.put("correo", correo);
		cuerpo.put("rol", rolPerfil);
		cuerpo.put("estado", "ACTIVO");
		try {
			Map<?, ?> respuesta = restClient
				.post()
				.uri("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generarTokenServicio())
				.body(cuerpo)
				.retrieve()
				.body(Map.class);
			Object id = respuesta == null ? null : respuesta.get("usuarioId");
			if (id instanceof Number numero) {
				return numero.longValue();
			}
			throw new ResponseStatusException(
				HttpStatus.BAD_GATEWAY,
				"msvc-user-service no devolvió el id del perfil"
			);
		} catch (RestClientResponseException e) {
			// El user-service responde {"error": "..."}: se reenvía ese mensaje al frontend.
			String mensaje = "No se pudo crear el perfil del usuario";
			try {
				Map<?, ?> error = e.getResponseBodyAs(Map.class);
				if (error != null && error.get("error") != null) {
					mensaje = String.valueOf(error.get("error"));
				}
			} catch (RuntimeException ignorada) {
				// cuerpo sin JSON: se mantiene el mensaje genérico
			}
			HttpStatus estado = e.getStatusCode().is4xxClientError()
				? HttpStatus.BAD_REQUEST
				: HttpStatus.BAD_GATEWAY;
			throw new ResponseStatusException(estado, mensaje);
		} catch (RestClientException e) {
			throw new ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE,
				"msvc-user-service no está disponible; la cuenta no se creó"
			);
		}
	}
}
