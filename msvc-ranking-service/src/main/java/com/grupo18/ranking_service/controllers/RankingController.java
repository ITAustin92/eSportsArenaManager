package com.grupo18.ranking_service.controllers;

import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.ranking_service.services.RankingService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rankings V1", description = "Tabla de posiciones por torneo")
@RestController
@RequestMapping("/api/v1/rankings")
@Tag(name = "Ranking V1", description = "Metodos CRUD para la gestión del ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @PostMapping("/update")
    @Operation(summary = "Actualizacion del ranking", description = "Se actualiza el ranking")
    @ApiResponse(responseCode = "200", description = "Suma puntos al ganador y registra la derrota del perdedor")
    public ResponseEntity<Void> updateRanking(@Valid @RequestBody MatchResultUpdateDTO updateDTO) {
        rankingService.processMatchResult(updateDTO);

        return ResponseEntity.ok().build(); }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Tabla de posiciones de un torneo")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<Ranking>> getTournamentLeaderboard(
            @Parameter(description = "ID del torneo", required = true, example = "1")
            @PathVariable Long tournamentId) {
        List<Ranking> leaderboard = rankingService.getTournamentLeaderboard(tournamentId);
        return ResponseEntity.ok(leaderboard);}


    @GetMapping("/tournament/{tournamentId}/team/{teamId}")
    @Operation(summary = "Posición de un equipo en un torneo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<Ranking> getTeamPosition(
            @Parameter(description = "ID del torneo", required = true, example = "1")
            @PathVariable Long tournamentId,
            @Parameter(description = "ID del torneo", required = true, example = "1")
            @PathVariable Long teamId) {
        return rankingService.findByTournamentAndTeam(tournamentId, teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}