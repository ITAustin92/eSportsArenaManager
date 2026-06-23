package com.grupo18.result_service.controllers;

import com.grupo18.result_service.models.Result;
import com.grupo18.result_service.services.ResultService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Results V1", description = "Resultados de partidas")
@RestController
@RequestMapping("/api/v1/results")
@Tag(name = "Resultado V1", description = "Metodos CRUD para la gestión de resultados")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping
    @Operation(
            summary = "Listado completo de resultados",
            description = "Devuelve una lista con todos los resultados registrados en la base de datos"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de resultados devuelta exitosamente"
    )
    public ResponseEntity<List<Result>> findAll() {
        return ResponseEntity.ok(resultService.findAll());
    }

    @PostMapping
    @Operation(summary = "Guardado del resultado", description = "Método que guarda el resultado en la DB")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Resultado a guardar", required = true,
            content = @Content(schema = @Schema(implementation = Result.class))
    )
    @ApiResponse(responseCode = "201", description = "Resultado guardado")
    public ResponseEntity<Result> save(@Valid @RequestBody Result result) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resultService.save(result));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de un resultado por id",
            description = "Se devuelve un resultado, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultado encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Resultado no se encuentra en la BD")
    })
    public ResponseEntity<Result> findById(@Parameter(description = "ID del Resultado a buscar", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(resultService.findById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizacion de resultado", description = "Se actualizan los datos de un resultado existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado actualizado"),
            @ApiResponse(responseCode = "404", description = "Resultado no se encuentra en la BD")
    })
    public ResponseEntity<Result> update(@Parameter(description = "ID del resultado a actualizar", required = true, example = "1") @PathVariable Long id, @Valid @RequestBody Result result) {
        return ResponseEntity.ok(resultService.updateById(id, result));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación de un resultado", description = "Se elimina resultado de la base de datos")
    @ApiResponse(responseCode = "204", description = "Resultado eliminado")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del resultado a eliminar", required = true, example = "1") @PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/match/{matchId}")
    @Operation(
            summary = "Busqueda de resultado por partida",
            description = "Se devuelve el resultado de la partida, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partida encontrada"),
            @ApiResponse(responseCode = "404", description = "Partida no se encuentra en la BD")
    })
    public ResponseEntity<Result> findByMatchId(@Parameter(description = "ID de partida a buscar", required = true, example = "1") @PathVariable Long matchId) {
        return ResponseEntity.ok(resultService.findByMatchId(matchId));
    }


    @GetMapping("/winner/{teamId}")
    @Operation(
            summary = "Busqueda de resultados por equipos ganadores",
            description = "Se devuelve una lista de resultados segun equipos ganadores, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo encontrada"),
            @ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD")
    })
    public ResponseEntity<List<Result>> findByWinnerTeamId(@Parameter(description = "ID del equipo ganador", required = true, example = "1") @PathVariable Long teamId) {
        return ResponseEntity.ok(resultService.findByWinnerTeamId(teamId));
    }
}