package com.grupo18.user_service.controllers;

import com.grupo18.user_service.models.User;
import com.grupo18.user_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Users V1", description = "CRUD de jugadores, organizadores y administradores")
public class UserController {

    @Autowired private UserService userService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> findById(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/nickname/{nickname}")
    @Operation(summary = "Buscar usuario por nickname")
    public ResponseEntity<User> findByNickname(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByNickname(nickname));
    }

    @GetMapping("/correo/{correo}")
    @Operation(summary = "Buscar usuario por correo")
    public ResponseEntity<User> findByCorreo(@PathVariable String correo) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByCorreo(correo));
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    public ResponseEntity<User> save(@Valid @RequestBody User usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<User> update(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody User usuario) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateById(id, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Filtrar usuarios por rol")
    public ResponseEntity<List<User>> findByRol(@PathVariable String rol) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Filtrar usuarios por estado")
    public ResponseEntity<List<User>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByEstado(estado));
    }
}
