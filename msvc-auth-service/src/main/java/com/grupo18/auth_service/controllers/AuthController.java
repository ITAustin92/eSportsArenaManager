package com.grupo18.auth_service.controllers;

import com.grupo18.auth_service.dtos.*;
import com.grupo18.auth_service.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
	name = "Autenticación",
	description = "Registro, login, renovación del JWT y gestión de cuentas"
)
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@Operation(
		summary = "Registrar jugador (público)",
		description = "Crea una cuenta ROLE_JUGADOR, su perfil en user-service y devuelve la sesión (token JWT)."
	)
	@ApiResponse(responseCode = "201", description = "Cuenta creada y sesión iniciada")
	@ApiResponse(
		responseCode = "400",
		description = "Datos inválidos o perfil rechazado por user-service"
	)
	@ApiResponse(responseCode = "409", description = "El usuario ya existe")
	@ApiResponse(responseCode = "503", description = "user-service no disponible")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	@Operation(
		summary = "Iniciar sesión (público)",
		description = "Valida usuario y contraseña (BCrypt) y devuelve el token JWT"
	)
	@ApiResponse(responseCode = "200", description = "Sesión iniciada")
	@ApiResponse(responseCode = "400", description = "Faltan datos")
	@ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/refresh")
	@SecurityRequirement(name = "bearer")
	@Operation(
		summary = "Renovar token",
		description = "Con un token vigente entrega uno nuevo. Un token vencido responde 401."
	)
	@ApiResponse(responseCode = "200", description = "Token renovado")
	@ApiResponse(responseCode = "401", description = "Sin token, token vencido o inválido")
	public ResponseEntity<AuthResponse> refresh(Authentication autenticacion) {
		return ResponseEntity.ok(authService.renovar(autenticacion.getName()));
	}

	@GetMapping("/me")
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Cuenta de la sesión actual")
	public ResponseEntity<CuentaResponse> me(Authentication autenticacion) {
		return ResponseEntity.ok(authService.perfil(autenticacion.getName()));
	}

	@GetMapping("/users")
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Listar cuentas (ADMIN)")
	@ApiResponse(responseCode = "200", description = "Listado de cuentas sin contraseñas")
	@ApiResponse(responseCode = "403", description = "El rol no es ADMIN")
	public ResponseEntity<List<CuentaResponse>> listar() {
		return ResponseEntity.ok(authService.listarCuentas());
	}

	@PostMapping("/users")
	@SecurityRequirement(name = "bearer")
	@Operation(
		summary = "Crear cuenta con rol (ADMIN)",
		description = "Roles: ROLE_ADMIN, ROLE_ORGANIZADOR, ROLE_JUGADOR"
	)
	@ApiResponse(responseCode = "201", description = "Cuenta creada")
	@ApiResponse(responseCode = "403", description = "El rol no es ADMIN")
	@ApiResponse(responseCode = "409", description = "El usuario ya existe")
	public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CrearCuentaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.crearCuentaAdmin(request));
	}

	@DeleteMapping("/users/{id}")
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Eliminar cuenta (ADMIN)")
	@ApiResponse(responseCode = "204", description = "Cuenta eliminada")
	@ApiResponse(responseCode = "409", description = "No se puede eliminar la propia cuenta")
	public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication autenticacion) {
		authService.eliminarCuenta(id, autenticacion.getName());
		return ResponseEntity.noContent().build();
	}
}
