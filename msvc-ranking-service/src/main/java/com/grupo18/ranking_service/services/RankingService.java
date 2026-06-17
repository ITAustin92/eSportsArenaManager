package com.grupo18.ranking_service.services;

import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import java.util.List;
import java.util.Optional;

public interface RankingService {
    void processMatchResult(MatchResultUpdateDTO updateDTO);

    List<Ranking> getTournamentLeaderboard(Long tournamentId);

    Optional<Ranking> findByTournamentAndTeam(Long tournamentId, Long teamId);
}