package com.grupo18.game_service.controllers;

import com.grupo18.game_service.assemblers.GameModelAssembler;
import com.grupo18.game_service.models.Game;
import com.grupo18.game_service.models.dtos.GameDTO;
import com.grupo18.game_service.services.GameService;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// V2: respuestas HATEOAS — EntityModel = 1 recurso + sus enlaces, CollectionModel = lista + enlaces
@RestController
@RequestMapping("/api/v2/games")
@Validated
@Tag(name = "Games V2", description = "CRUD de videojuegos con respuestas HATEOAS")
public class GameControllerV2 {

    @Autowired private GameService gameService;
    @Autowired private GameModelAssembler gameModelAssembler;

    @GetMapping
    @Operation(summary = "Listar todos los juegos", description = "Devuelve la lista de juegos con enlaces HATEOAS")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<CollectionModel<EntityModel<Game>>> findAll() {
        List<EntityModel<Game>> models = gameService.findAll()
                .stream().map(gameModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(GameControllerV2.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar juego por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado",
                    content = @Content(schema = @Schema(implementation = GameDTO.class))),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    public ResponseEntity<EntityModel<Game>> findById(
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(gameModelAssembler.toModel(gameService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear juego")
    public ResponseEntity<EntityModel<Game>> save(@Valid @RequestBody Game juego) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gameModelAssembler.toModel(gameService.save(juego)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar juego")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego actualizado"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    public ResponseEntity<EntityModel<Game>> update(
            @Parameter(description = "ID del juego", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody Game juego) {
        return ResponseEntity.ok(gameModelAssembler.toModel(gameService.updateById(id, juego)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar juego")
    @ApiResponse(responseCode = "204", description = "Juego desactivado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del juego", required = true, example = "1") @PathVariable Long id) {
        gameService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
