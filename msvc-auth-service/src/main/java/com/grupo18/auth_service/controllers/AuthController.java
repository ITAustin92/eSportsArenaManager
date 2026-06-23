package com.grupo18.auth_service.controllers;
import com.grupo18.auth_service.dtos.*;
import com.grupo18.auth_service.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro y login. Devuelve el JWT que usa todo el sistema.")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un usuario y devuelve su token. Roles válidos: ROLE_ADMIN, ROLE_ORGANIZADOR, ROLE_JUGADOR")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida usuario y clave, devuelve el token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
