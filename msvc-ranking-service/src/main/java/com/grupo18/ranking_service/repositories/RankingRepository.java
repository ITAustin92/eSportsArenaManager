package com.grupo18.ranking_service.repositories;

import com.grupo18.ranking_service.models.Ranking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
	List<Ranking> findByTournamentIdOrderByPointsDesc(Long tournamentId);

	Optional<Ranking> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);
	Optional<Ranking> findByTournamentIdAndUserId(Long tournamentId, Long userId);
}
