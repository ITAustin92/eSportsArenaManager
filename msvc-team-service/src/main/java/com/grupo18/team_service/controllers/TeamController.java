package com.grupo18.team_service.controllers;

import com.grupo18.team_service.models.Team;
import com.grupo18.team_service.services.TeamService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Teams V1", description = "CRUD de equipos y miembros")
@RestController
@RequestMapping("/api/v1/teams")
@Validated
@Tag(name = "Equipo V1", description = "Metodos CRUD para la gestión de equipos")
public class TeamController {

    @Autowired
    private TeamService teamService;


    @GetMapping
    @Operation(
            summary = "Listado de todos los equipos",
            description = "Se devuelve una lista con todos los equipos en la tabla Team de la DB"
    )
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")

    public ResponseEntity<List<Team>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findAll());
    }


    @PostMapping
    @Operation(summary = "Guardado del equipo",
               description = "Método que guarda al equipo en la DB"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Equipo a crear", required = true,
            content = @Content(schema = @Schema(implementation = Team.class))
    )
    public ResponseEntity<Team> save(@Valid @RequestBody Team equipo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teamService.save(equipo));
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de un equipo por id",
            description = "Se devuelve un equipo, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Torneo encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD")
    })
    public ResponseEntity<Team> findById(@Parameter(description = "ID del Equipo a buscar", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findById(id));
    }


    @GetMapping("/juego/{juegoId}")
    @Operation(
            summary = "Busqueda de equipos por juego",
            description = "Se devuelve una lista de equipos por juego, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado"),
            @ApiResponse(responseCode = "404", description = "Juego no se encuentra en la BD")
    })
    public ResponseEntity<List<Team>> findByJuego(@Parameter(description = "ID del juego a buscar", required = true, example = "1") @PathVariable Long juegoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByJuegoPrincipalId(juegoId));
    }


    @GetMapping("/capitan/{capitanId}")
    @Operation(
            summary = "Busqueda de equipo por capitán",
            description = "Se devuelve una lista de equipos por su capitán, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capitán encontrado"),
            @ApiResponse(responseCode = "404", description = "Capitán no se encuentra en la BD")
    })
    public ResponseEntity<List<Team>> findByCapitan(@Parameter(description = "ID del capitán a buscar", required = true, example = "1") @PathVariable Long capitanId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByCapitanId(capitanId));
    }


    @GetMapping("/estado/{estado}")
    @Operation(
            summary = "Busqueda de equipo por estado",
            description = "Se devuelve una lista de equipos por su estado, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipos encontrado"),
            @ApiResponse(responseCode = "404", description = "Equipos con el estado ingresado, no se encuentran en la BD")
    })
    public ResponseEntity<List<Team>> findByEstado(@Parameter(description = "Estado del equipo a buscar", required = true, example = "ACTIVO") @PathVariable String estado) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByEstado(estado));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizacion de equipo", description = "Se actualizan los datos de un equipo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo actualizado"),
            @ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD")
    })
    public ResponseEntity<Team> update(@Parameter(description = "ID del equipo a actualizar", required = true, example = "1") @PathVariable Long id, @Valid @RequestBody Team equipo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.updateById(id, equipo));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación de un equipo", description = "Se elimina un equipo de la base de datos")
    @ApiResponse(responseCode = "204", description = "Torneo eliminado")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del equipo a eliminar", required = true, example = "1") @PathVariable Long id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}