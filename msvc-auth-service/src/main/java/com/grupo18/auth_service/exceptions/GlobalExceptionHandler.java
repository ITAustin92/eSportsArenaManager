package com.grupo18.auth_service.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// Todas las respuestas de error tienen la forma {"error": "...", "campos": {...}} para que el frontend
// muestre un mensaje entendible.
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleConflict(
		org.springframework.dao.DataIntegrityViolationException ex
	) {
		return ResponseEntity.status(409).body(
			Map.of("error", "El registro ya existe o está relacionado con otros datos")
		);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> manejarEstado(ResponseStatusException ex) {
		Map<String, Object> error = new LinkedHashMap<>();
		error.put("error", ex.getReason() == null ? "Solicitud rechazada" : ex.getReason());
		return ResponseEntity.status(ex.getStatusCode()).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		ex.getBindingResult()
			.getFieldErrors()
			.forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
		Map<String, Object> error = new LinkedHashMap<>();
		error.put("error", campos.isEmpty() ? "Datos inválidos" : campos.values().iterator().next());
		error.put("campos", campos);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> manejarCuerpo(HttpMessageNotReadableException ex) {
		Map<String, Object> error = new LinkedHashMap<>();
		error.put("error", "El cuerpo de la petición no es un JSON válido");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
}
