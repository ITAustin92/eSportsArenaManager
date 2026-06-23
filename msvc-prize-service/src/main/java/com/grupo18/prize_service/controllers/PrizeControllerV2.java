package com.grupo18.prize_service.controllers;

import com.grupo18.prize_service.assemblers.PrizeModelAssembler;
import com.grupo18.prize_service.models.Prize;
import com.grupo18.prize_service.services.PrizeService;
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

// V2 — HATEOAS. La distribución de premios es una acción de negocio (no un PUT genérico).
@RestController
@RequestMapping("/api/v2/prizes")
@Validated
@Tag(name = "Prizes V2", description = "Premios de torneos con respuestas HATEOAS")
public class PrizeControllerV2 {

    @Autowired private PrizeService prizeService;
    @Autowired private PrizeModelAssembler prizeModelAssembler;

    @GetMapping
    @Operation(summary = "Listar premios")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Prize>>> findAll() {
        List<EntityModel<Prize>> models = prizeService.findAll().stream().map(prizeModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(PrizeControllerV2.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar premio por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<EntityModel<Prize>> findById(
            @Parameter(description = "ID", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(prizeModelAssembler.toModel(prizeService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear premio")
    public ResponseEntity<EntityModel<Prize>> save(@Valid @RequestBody Prize prize) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prizeModelAssembler.toModel(prizeService.save(prize)));
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Premios de un torneo")
    public ResponseEntity<CollectionModel<EntityModel<Prize>>> findByTournament(
            @Parameter(description = "ID del torneo", required = true, example = "1") @PathVariable Long tournamentId) {
        List<EntityModel<Prize>> models = prizeService.findByTournamentId(tournamentId).stream().map(prizeModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(PrizeControllerV2.class).findByTournament(tournamentId)).withSelfRel()));
    }

    @PostMapping("/tournament/{tournamentId}/distribute")
    @Operation(summary = "Distribuir premios", description = "Asigna premios según el ranking final del torneo")
    public ResponseEntity<String> distributePrizes(
            @Parameter(description = "ID del torneo", required = true, example = "1") @PathVariable Long tournamentId) {
        prizeService.distributePrizesForTournament(tournamentId);
        return ResponseEntity.ok("Premios del torneo " + tournamentId + " distribuidos exitosamente.");
    }
}
