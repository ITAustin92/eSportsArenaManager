package com.grupo18.ranking_service.controllers;

import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.ranking_service.services.RankingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rankings")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @PostMapping("/update")
    public ResponseEntity<Void> updateRanking(@Valid @RequestBody MatchResultUpdateDTO updateDTO) {// Le pasamos el sobre al servicio para que haga la matemática
        rankingService.processMatchResult(updateDTO);
        // Devolvemos un simple "OK" (200) sin contenido, // ya que el result-service solo necesita saber que se guardó bien.
        return ResponseEntity.ok().build(); }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Ranking>> getTournamentLeaderboard(@PathVariable Long tournamentId) {// El servicio llamará al repositorio, el cual ya tiene la instrucción de ordenar de mayor a menor
        List<Ranking> leaderboard = rankingService.getTournamentLeaderboard(tournamentId);
        return ResponseEntity.ok(leaderboard);}

    // Cumple el requisito: "Buscar posición por participante"
    @GetMapping("/tournament/{tournamentId}/team/{teamId}")
    public ResponseEntity<Ranking> getTeamPosition(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId) {
        return rankingService.findByTournamentAndTeam(tournamentId, teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}