package com.grupo18.ranking_service.services;

import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import java.util.List;
import java.util.Optional;

public interface RankingService {
    // Procesa los puntos a partir de un resultado de partido
    void processMatchResult(MatchResultUpdateDTO updateDTO);

    // Obtiene la tabla de posiciones ordenada
    List<Ranking> getTournamentLeaderboard(Long tournamentId);

    Optional<Ranking> findByTournamentAndTeam(Long tournamentId, Long teamId);
}