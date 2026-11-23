package com.grupo18.registration_service.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.grupo18.registration_service")
public class GlobalExceptionHandler {

	@ExceptionHandler(RegistrationException.class)
	public ResponseEntity<Map<String, String>> handleGameException(RegistrationException ex) {
		Map<String, String> error = new HashMap<>();
		error.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(feign.FeignException.class)
	public ResponseEntity<Map<String, String>> handleRemote(feign.FeignException ex) {
		int status = ex.status() >= 400 && ex.status() < 500 ? ex.status() : 503;
		String mensaje =
			status == 404
				? "El recurso solicitado no existe en el servicio relacionado"
				: status == 403
					? "No tienes permiso para consultar el recurso relacionado"
					: status == 401
						? "La sesión no es válida para el servicio relacionado"
						: "Un servicio relacionado no está disponible o rechazó la solicitud";
		return ResponseEntity.status(status).body(Map.of("error", mensaje));
	}

	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleConflict(
		org.springframework.dao.DataIntegrityViolationException ex
	) {
		return ResponseEntity.status(409).body(
			Map.of("error", "El registro ya existe o está relacionado con otros datos")
		);
	}

	// Errores con código HTTP explícito (por ejemplo 403 cuando un jugador intenta modificar algo ajeno).
	@ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
	public ResponseEntity<Map<String, String>> handleStatus(
		org.springframework.web.server.ResponseStatusException ex
	) {
		Map<String, String> error = new HashMap<>();
		error.put("error", ex.getReason() == null ? "Solicitud rechazada" : ex.getReason());
		return ResponseEntity.status(ex.getStatusCode()).body(error);
	}

	// Validaciones @Valid: 400 con el primer mensaje legible (antes caían en el 500 genérico).
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(
		org.springframework.web.bind.MethodArgumentNotValidException ex
	) {
		Map<String, Object> error = new HashMap<>();
		String mensaje = ex
			.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(f -> f.getDefaultMessage())
			.findFirst()
			.orElse("Datos inválidos");
		error.put("error", mensaje);
		error.put(
			"campos",
			ex
				.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(
					java.util.stream.Collectors.toMap(
						f -> f.getField(),
						f -> String.valueOf(f.getDefaultMessage()),
						(a, b) -> a
					)
				)
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> handleBody(
		org.springframework.http.converter.HttpMessageNotReadableException ex
	) {
		Map<String, String> error = new HashMap<>();
		error.put(
			"error",
			"El cuerpo de la petición no es válido (revisa formatos de fecha y números)"
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Error interno del servidor");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
