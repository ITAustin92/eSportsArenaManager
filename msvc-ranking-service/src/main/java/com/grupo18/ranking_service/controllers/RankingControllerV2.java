package com.grupo18.ranking_service.controllers;

import com.grupo18.ranking_service.assemblers.RankingModelAssembler;
import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.ranking_service.services.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// V2 — HATEOAS. El ranking se actualiza automáticamente al validar resultados (no es un CRUD clásico).
@RestController
@RequestMapping("/api/v2/rankings")
@Validated
@Tag(name = "Rankings V2", description = "Tabla de posiciones con respuestas HATEOAS")
public class RankingControllerV2 {

    @Autowired private RankingService rankingService;
    @Autowired private RankingModelAssembler rankingModelAssembler;

    @PostMapping("/update")
    @Operation(summary = "Actualizar ranking tras un resultado", description = "Suma puntos al ganador y registra la derrota del perdedor")
    public ResponseEntity<Void> updateRanking(@Valid @RequestBody MatchResultUpdateDTO updateDTO) {
        rankingService.processMatchResult(updateDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Tabla de posiciones de un torneo")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Ranking>>> getTournamentLeaderboard(
            @Parameter(description = "ID del torneo", required = true, example = "1") @PathVariable Long tournamentId) {
        List<EntityModel<Ranking>> models = rankingService.getTournamentLeaderboard(tournamentId)
                .stream().map(rankingModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(RankingControllerV2.class).getTournamentLeaderboard(tournamentId)).withSelfRel()));
    }

    @GetMapping("/tournament/{tournamentId}/team/{teamId}")
    @Operation(summary = "Posición de un equipo en un torneo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<EntityModel<Ranking>> getTeamPosition(
            @Parameter(description = "ID del torneo", required = true, example = "1") @PathVariable Long tournamentId,
            @Parameter(description = "ID del equipo", required = true, example = "1") @PathVariable Long teamId) {
        return rankingService.findByTournamentAndTeam(tournamentId, teamId)
                .map(rankingModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
