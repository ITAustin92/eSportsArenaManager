package com.grupo18.ranking_service.repositories;

import com.grupo18.ranking_service.models.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    List<Ranking> findByTournamentIdOrderByPointsDesc(Long tournamentId);

    Optional<Ranking> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);
}