package com.grupo18.user_service.controllers;

import com.grupo18.user_service.assemblers.UserModelAssembler;
import com.grupo18.user_service.models.User;
import com.grupo18.user_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/users")
@Validated
@Tag(name = "Users V2", description = "CRUD de usuarios con respuestas HATEOAS")
public class UserControllerV2 {

    @Autowired private UserService userService;
    @Autowired private UserModelAssembler userModelAssembler;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<CollectionModel<EntityModel<User>>> findAll() {
        List<EntityModel<User>> models = userService.findAll().stream().map(userModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(UserControllerV2.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<User>> findById(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(userModelAssembler.toModel(userService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    public ResponseEntity<EntityModel<User>> save(@Valid @RequestBody User usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userModelAssembler.toModel(userService.save(usuario)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<User>> update(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody User usuario) {
        return ResponseEntity.ok(userModelAssembler.toModel(userService.updateById(id, usuario)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
