package com.grupo18.prize_service.services;

import com.grupo18.prize_service.models.Prize;
import java.util.List;

public interface PrizeService {

    // CRUD Básico
    Prize save(Prize prize);
    Prize findById(Long id);
    List<Prize> findAll();

    // Filtros
    List<Prize> findByTournamentId(Long tournamentId);
    List<Prize> findByTeamId(Long teamId);

    // REGLA DE NEGOCIO PRINCIPAL: Repartir los premios a los ganadores
    void distributePrizesForTournament(Long tournamentId);
}