package com.grupo18.prize_service.repositories;

import com.grupo18.prize_service.models.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeRepository extends JpaRepository<Prize, Long> {


    List<Prize> findByTournamentId(Long tournamentId);

    List<Prize> findByTeamId(Long teamId);

    List<Prize> findByTournamentIdAndStatus(Long tournamentId, String status);
}