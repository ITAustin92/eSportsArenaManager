package com.grupo18.user_service.controllers;

import com.grupo18.user_service.assemblers.UserModelAssembler;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/users")
@Validated
@Tag(name = "Usuarios V2", description = "Metodos CRUD HATEOAS para la gestión de usuario")
public class UserControllerV2{

    @Autowired
    private UserService userService;

    @Autowired
    private UserModelAssembler userModelAssembler;

    @GetMapping
    @Operation(
            summary = "Listado de todos los usuarios",
            description = "Se devuelve una lista con todos los usuarios en la tabla usuarios de la DB"
    )
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<CollectionModel<EntityModel<User>>> findAll() {
        List<EntityModel<User>> entityModels = userService.findAll()
                .stream()
                .map(userModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<User>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(UserControllerV2.class).findAll()).withSelfRel()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(collectionModel);
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
            @ApiResponse(responseCode = "404", description = "Paciente no se encuentra en la BD")
    })
    public ResponseEntity<EntityModel<User>> findById(@Parameter(description = "ID del Usuario a buscar", required = true, example = "1") @PathVariable Long id) {
        EntityModel<User> entityModel = userModelAssembler.toModel(userService.findById(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityModel);
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
    public ResponseEntity<EntityModel<User>> findByNickname(@PathVariable String nickname) {
        EntityModel<User> entityModel = userModelAssembler.toModel(userService.findByNickname(nickname));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityModel);
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
    public ResponseEntity<EntityModel<User>> findByCorreo(@Parameter(description = "correo del Usuario a buscar", required = true, example = "usuario@correo.cl")@PathVariable String correo) {
        EntityModel<User> entityModel = userModelAssembler.toModel(userService.findByCorreo(correo));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityModel);
    }

    @PostMapping
    @Operation(summary = "Guardado del usuario", description = "Método que guarda al usuario en la tabla")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Usuario a crear", required = true,
            content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(responseCode = "201", description = "Paciente creado")
    public ResponseEntity<EntityModel<User>> save(@Valid @RequestBody User usuario) {
        EntityModel<User> entityModel = userModelAssembler.toModel(userService.save(usuario));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entityModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizacion de usuario", description = "Se actualizan los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<EntityModel<User>> update(@Parameter(description = "ID del Usuario a actualizar", required = true, example = "1")@PathVariable Long id, @Valid @RequestBody User usuario) {
        EntityModel<User> entityModel = userModelAssembler.toModel(userService.updateById(id, usuario));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityModel);
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
    public ResponseEntity<CollectionModel<EntityModel<User>>> findByRol(@Parameter(description = "Rol del Usuario a buscar", required = true, example = "jugador")@PathVariable String rol) {
        List<EntityModel<User>> entityModels = userService.findByRol(rol)
                .stream()
                .map(userModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<User>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(UserControllerV2.class).findByRol(rol)).withSelfRel()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(collectionModel);
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
    public ResponseEntity<CollectionModel<EntityModel<User>>> findByEstado(@Parameter(description = "estado del Usuario a buscar", required = true, example = "ACTIVO")@PathVariable String estado) {
        List<EntityModel<User>> entityModels = userService.findByEstado(estado)
                .stream()
                .map(userModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<User>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(UserControllerV2.class).findByEstado(estado)).withSelfRel()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(collectionModel);
    }

}

