package com.grupo18.tournament_service.controllers;

import com.grupo18.tournament_service.models.Tournament;
import com.grupo18.tournament_service.services.TournamentService;
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
@RequestMapping("/api/v1/tournaments")
@Validated
@Tag(name = "Torneos V1", description = "Metodos CRUD para la gestión de torneos")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    @Operation(
            summary = "Listado de todos los torneos",
            description = "Se devuelve una lista con todos los torneos en la tabla torneos de la DB"
    )
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Tournament>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findAll());
    }


    @PostMapping
    @Operation(summary = "Guardado del torneo", description = "Método que guarda al torneo en la DB")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Torneo a crear", required = true,
            content = @Content(schema = @Schema(implementation = Tournament.class))
    )
    public ResponseEntity<Tournament> save(@Valid @RequestBody Tournament tournament) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tournamentService.save(tournament));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de un torneo por id",
            description = "Se devuelve un torneo, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Torneo encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tournament.class))),
            @ApiResponse(responseCode = "404", description = "Torneo no se encuentra en la BD")
    })
    public ResponseEntity<Tournament> findById(@Parameter(description = "ID del Torneo a buscar", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findById(id));
    }


    @GetMapping("/game/{gameId}")
    @Operation(
            summary = "Busqueda de torneos por juego",
            description = "Se devuelve una lista de torneos por juego, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado"),
            @ApiResponse(responseCode = "404", description = "Juego no se encuentra en la BD")
    })
    public ResponseEntity<List<Tournament>> findByGameId(@Parameter(description = "ID del juego a buscar", required = true, example = "1") @PathVariable Long gameId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByGameId(gameId));
    }


    @GetMapping("/organizer/{organizerId}")
    @Operation(
            summary = "Busqueda de torneos por organizador",
            description = "Se devuelve una lista de torneos por organizador, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organizador encontrado"),
            @ApiResponse(responseCode = "404", description = "Organizador no se encuentra en la BD")
    })
    public ResponseEntity<List<Tournament>> findByOrganizerId(@Parameter(description = "ID del organizador a buscar", required = true, example = "1") @PathVariable Long organizerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByOrganizerId(organizerId));
    }


    @GetMapping("/state/{state}")
    @Operation(
            summary = "Busqueda de torneos por estado",
            description = "Se devuelve una lista de torneos, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneos por estado encontrados"),
            @ApiResponse(responseCode = "404", description = "Estado no se encuentra en la BD")
    })
    public ResponseEntity<List<Tournament>> findByState(@Parameter(description = "Estado de los torneos a buscar", required = true, example = "Iniciado") @PathVariable String state) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByState(state));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizacion de torneo", description = "Se actualizan los datos de un torneo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo actualizado"),
            @ApiResponse(responseCode = "404", description = "Torneo no se encuentra en la BD")
    })
    public ResponseEntity<Tournament> update(@Parameter(description = "ID del Torneo a actualizar", required = true, example = "1") @PathVariable Long id, @Valid @RequestBody Tournament tournament) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.updateById(id, tournament));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Borrado del torneo", description = "Se elimina un torneo de la base de datos")
    @ApiResponse(responseCode = "204", description = "Torneo eliminado")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del Torneo a eliminar", required = true, example = "1") @PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}