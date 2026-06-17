package com.grupo18.prize_service.services;

import com.grupo18.prize_service.models.Prize;
import java.util.List;

public interface PrizeService {

    Prize save(Prize prize);
    Prize findById(Long id);
    List<Prize> findAll();

    List<Prize> findByTournamentId(Long tournamentId);
    List<Prize> findByTeamId(Long teamId);

    void distributePrizesForTournament(Long tournamentId);
}