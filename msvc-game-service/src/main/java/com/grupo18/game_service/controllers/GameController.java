package com.grupo18.game_service.controllers;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@Validated
@Tag(name = "Games V1", description = "CRUD de videojuegos habilitados para torneos")
public class GameController {

    @Autowired private GameService gameService;

    @GetMapping
    @Operation(summary = "Listar juegos activos", description = "Devuelve todos los juegos con estado ACTIVO")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<List<Game>> findActivos() {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.findByEstado("ACTIVO"));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar juegos por estado")
    public ResponseEntity<List<Game>> findByEstado(
            @Parameter(description = "Estado: ACTIVO | INACTIVO", required = true) @PathVariable String estado) {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.findByEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar juego por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado",
                    content = @Content(schema = @Schema(implementation = GameDTO.class))),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    public ResponseEntity<Game> findById(
            @Parameter(description = "ID del juego", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear juego")
    public ResponseEntity<Game> save(@Valid @RequestBody Game juego) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.save(juego));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar juego")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego actualizado"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    public ResponseEntity<Game> update(
            @Parameter(description = "ID del juego", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody Game juego) {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.updateById(id, juego));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar juego (soft delete)")
    @ApiResponse(responseCode = "204", description = "Juego desactivado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del juego", required = true, example = "1") @PathVariable Long id) {
        gameService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
