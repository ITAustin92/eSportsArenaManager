package com.grupo18.match_service.controllers;

import com.grupo18.match_service.models.Match;
import com.grupo18.match_service.services.MatchService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Matches V1", description = "Partidas dentro de torneos")
@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Partidas V1", description = "Metodos CRUD para la gestión de partidas")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PutMapping("/{id}")
    @Operation(summary = "Actualizacion de partido", description = "Se actualizan los datos de un partido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partido actualizado"),
            @ApiResponse(responseCode = "404", description = "Partido no se encuentra en la BD")
    })
    public ResponseEntity<Match> update(@Parameter(description = "ID del Partido a actualizar", required = true, example = "1") @PathVariable Long id, @Valid @RequestBody Match match) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(matchService.updateById(id, match));
    }

    // Crear un nuevo partido
    // Se delega al Service toda la validación de inscripciones y torneo
    @PostMapping
    @Operation(summary = "Guardado del partido", description = "Método que guarda el partido en la DB")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Partido a crear", required = true,
            content = @Content(schema = @Schema(implementation = Match.class))
    )
    @ApiResponse(responseCode = "201", description = "Partido creado")
    public ResponseEntity<Match> save(@Valid @RequestBody Match match) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matchService.save(match));
    }

    // Buscar partido por ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Búsqueda de un partido por id",
            description = "Se devuelve un partido, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Partido encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Match.class))),
            @ApiResponse(responseCode = "404", description = "Partida no se encuentra en la BD")
    })
    public ResponseEntity<Match> findById(@Parameter(description="ID del partido",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    // Ruta semántica: Listar partidos de un torneo
    @GetMapping("/tournament/{tournamentId}")
    @Operation(
            summary = "Busqueda de un torneo por ID",
            description = "Se devuelve un torneo, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo encontrado"),
            @ApiResponse(responseCode = "404", description = "Torneo no se encuentra en la BD")
    })
    public ResponseEntity<List<Match>> findByTournamentId(@Parameter(description="ID del torneo",required=true,example="1") @PathVariable Long tournamentId) {
        return ResponseEntity.ok(matchService.findByTournamentId(tournamentId));
    }

    // Ruta semántica: Listar calendario de un equipo (sea local o visitante)
    @GetMapping("/team/{teamId}")
    @Operation(
            summary = "Busqueda de un equipo por ID",
            description = "Se devuelve un equipo, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo encontrado"),
            @ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD")
    })
    public ResponseEntity<List<Match>> findByTeamId(@Parameter(description="ID del equipo",required=true,example="1")@PathVariable Long teamId) {
        return ResponseEntity.ok(matchService.findByTeamId(teamId));
    }

    // Cumple el requisito: "Cancelar partida"
    @DeleteMapping("/{id}")
    @Operation(summary = "Borrado del partido", description = "Se elimina partido de la base de datos")
    @ApiResponse(responseCode = "204", description = "Partido eliminado")
    public ResponseEntity<Void> delete(@Parameter(description="ID del partido",required=true,example="1") @PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}