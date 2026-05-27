package com.grupo18.result_service.repositories;

import com.grupo18.result_service.models.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByMatchId(Long matchId);
    List<Result> findByWinnerTeamId(Long winnerTeamId);
    boolean existsByMatchId(Long matchId);
}