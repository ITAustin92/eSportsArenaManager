package com.grupo18.prize_service.controllers;

import com.grupo18.prize_service.models.Prize;
import com.grupo18.prize_service.services.PrizeService;
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

@Tag(name = "Prizes V1", description = "Premios de torneos")
@RestController
@RequestMapping("/api/v1/prizes")
@Tag(name = "Equipo V1", description = "Metodos CRUD para la gestión de equipos")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    @PostMapping
    @Operation(summary = "Guardado del premio", description = "Método que guarda el premio en la DB")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Premio a crear", required = true,
            content = @Content(schema = @Schema(implementation = Prize.class))
    )
    @ApiResponse(responseCode = "201", description = "Premio creado")
    public ResponseEntity<Prize> save(@Valid @RequestBody Prize prize) {
        Prize savedPrize = prizeService.save(prize);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPrize);
    }

    @GetMapping
    @Operation(
            summary = "Listado de todos los premios",
            description = "Se devuelve una lista con todos los premios en la tabla premio de la DB"
    )
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Prize>> findAll() {
        return ResponseEntity.ok(prizeService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de un premio por id",
            description = "Se devuelve un premio, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Premio encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Prize.class))),
            @ApiResponse(responseCode = "404", description = "Premio no se encuentra en la BD")
    })
    public ResponseEntity<Prize> findById(@Parameter(description = "ID del premio", required = true, example = "1")@PathVariable Long id) {
        return ResponseEntity.ok(prizeService.findById(id));
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Premios de un torneo")
    public ResponseEntity<List<Prize>> findByTournamentId(@Parameter(description = "ID del torneo", required = true, example = "1") @PathVariable Long tournamentId) {
        return ResponseEntity.ok(prizeService.findByTournamentId(tournamentId));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Premios de un equipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD")
    })
    public ResponseEntity<List<Prize>> findByTeamId(@Parameter(description = "ID del equipo", required = true, example = "1") @PathVariable Long teamId) {
        return ResponseEntity.ok(prizeService.findByTeamId(teamId));
    }

    @PostMapping("/tournament/{tournamentId}/distribute")
    @Operation(summary = "Distribuir premios", description = "Asigna premios según el ranking final del torneo")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exitoso"),
            @ApiResponse(responseCode = "404", description = "Torneo no se encuentra en la BD")
    })
    public ResponseEntity<String> distributePrizes(@Parameter(description = "ID del torneo", required = true, example = "1") @PathVariable Long tournamentId) {

        prizeService.distributePrizesForTournament(tournamentId);

        return ResponseEntity.ok("Los premios del torneo " + tournamentId + " han sido distribuidos exitosamente según el ranking.");
    }
}