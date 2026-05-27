package com.grupo18.sanction_service.services;

import com.grupo18.sanction_service.models.Sanction;
import java.util.List;

public interface SanctionService {


    Sanction save(Sanction sanction);
    Sanction findById(Long id);
    List<Sanction> findAll();
    Sanction updateStatus(Long id, String newStatus);
    void deleteById(Long id);

    List<Sanction> findByTournamentId(Long tournamentId);
    List<Sanction> findByTeamId(Long teamId);
    List<Sanction> findByUserId(Long userId);
}