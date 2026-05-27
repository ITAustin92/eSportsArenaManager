package com.grupo18.sanction_service.repositories;

import com.grupo18.sanction_service.models.Sanction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long> {
    List<Sanction> findByTournamentId(Long tournamentId);
    List<Sanction> findByTeamId(Long teamId);
    List<Sanction> findByUserId(Long userId);
    List<Sanction> findByTournamentIdAndUserId(Long tournamentId, Long userId);
}