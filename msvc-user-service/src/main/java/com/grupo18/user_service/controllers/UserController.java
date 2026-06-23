package com.grupo18.user_service.controllers;


import com.grupo18.user_service.models.User;
import com.grupo18.user_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Usuarios V1", description = "Metodos CRUD para la gestión de usuario")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(
            summary = "Listado de todos los usuarios",
            description = "Se devuelve una lista con todos los usuarios en la tabla usuarios de la DB"
    )
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de un usuario por id",
            description = "Se devuelve un usuario, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<User> findById(@Parameter(description = "ID del Usuario a buscar", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findById(id));
    }

    @GetMapping("/nickname/{nickname}")
    @Operation(
            summary = "Busqueda de un usuario por nickname",
            description = "Se devuelve un usuario, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<User> findByNickname(@Parameter(description = "Nickname del usuario a buscar", required = true, example = "usuario") @PathVariable String nickname) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByNickname(nickname));
    }

    @GetMapping("/correo/{correo}")
    @Operation(
            summary = "Busqueda de un usuario por correo",
            description = "Se devuelve un usuario, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<User> findByCorreo(@Parameter(description = "correo del Usuario a buscar", required = true, example = "usuario@correo.cl")@PathVariable String correo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByCorreo(correo));
    }

    @PostMapping
    @Operation(summary = "Guardado del usuario", description = "Método que guarda al usuario en la DB")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Usuario a crear", required = true,
            content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    public ResponseEntity<User> save(@Valid @RequestBody User usuario) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.save(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizacion de usuario", description = "Se actualizan los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<User> update(@Parameter(description = "ID del Usuario a actualizar", required = true, example = "1")@PathVariable Long id, @Valid @RequestBody User usuario) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateById(id, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrado del usuario", description = "Se elimina usuario de la base de datos")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del Usuario a eliminar", required = true, example = "1")@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    @Operation(
            summary = "Busqueda de un usuario por rol",
            description = "Se devuelve un usuario, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<List<User>> findByRol(@Parameter(description = "Rol del Usuario a buscar", required = true, example = "jugador")@PathVariable String rol) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    @Operation(
            summary = "Busqueda de un usuario por estado",
            description = "Se devuelve un usuario, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<List<User>> findByEstado(@Parameter(description = "estado del Usuario a buscar", required = true, example = "ACTIVO")@PathVariable String estado) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByEstado(estado));
    }
}