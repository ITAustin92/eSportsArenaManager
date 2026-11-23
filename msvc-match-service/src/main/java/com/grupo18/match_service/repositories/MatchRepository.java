package com.grupo18.match_service.repositories;

import com.grupo18.match_service.models.Match;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
	List<Match> findByTournamentId(Long tournamentId);

	List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

	List<Match> findByStatus(String status);
}
