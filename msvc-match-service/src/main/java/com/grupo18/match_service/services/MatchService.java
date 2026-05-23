package com.grupo18.match_service.services;

import com.grupo18.match_service.models.Match;
import java.util.List;

public interface MatchService {
    Match save(Match match);

    Match findById(Long id);

    List<Match> findByTournamentId(Long tournamentId);

    List<Match> findByTeamId(Long teamId);
}