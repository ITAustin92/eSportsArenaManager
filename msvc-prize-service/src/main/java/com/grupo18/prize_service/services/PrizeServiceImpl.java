package com.grupo18.prize_service.services;

import com.grupo18.prize_service.clients.RankingClient;
import com.grupo18.prize_service.clients.TournamentClient;
import com.grupo18.prize_service.models.Prize;
import com.grupo18.prize_service.models.dtos.RankingDTO;
import com.grupo18.prize_service.models.dtos.TournamentDTO;
import com.grupo18.prize_service.repositories.PrizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrizeServiceImpl implements PrizeService {

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private TournamentClient tournamentClient;

    @Autowired
    private RankingClient rankingClient;

    @Transactional
    @Override
    public Prize save(Prize prize) {

        TournamentDTO tournament = tournamentClient.getTournamentById(prize.getTournamentId());
        if (tournament == null) {
            throw new RuntimeException("Error: El torneo indicado no existe.");
        }

        return prizeRepository.save(prize);
    }

    @Transactional(readOnly = true)
    @Override
    public Prize findById(Long id) {
        return prizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Premio no encontrado"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Prize> findAll() {
        return prizeRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Prize> findByTournamentId(Long tournamentId) {
        return prizeRepository.findByTournamentId(tournamentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Prize> findByTeamId(Long teamId) {
        return prizeRepository.findByTeamId(teamId);
    }


    @Transactional
    @Override
    public void distributePrizesForTournament(Long tournamentId) {

        TournamentDTO tournament = tournamentClient.getTournamentById(tournamentId);
        if (tournament == null || !"FINISHED".equalsIgnoreCase(tournament.getState())) {
            throw new RuntimeException("Solo se pueden repartir premios cuando el torneo está en estado FINISHED.");
        }

        List<Prize> pendingPrizes = prizeRepository.findByTournamentIdAndStatus(tournamentId, "PENDING");
        if (pendingPrizes.isEmpty()) {
            return;
        }

        pendingPrizes.sort((p1, p2) -> Double.compare(p2.getAmount(), p1.getAmount()));

        List<RankingDTO> leaderboard = rankingClient.getTournamentLeaderboard(tournamentId);

        int prizesToDistribute = Math.min(pendingPrizes.size(), leaderboard.size());

        for (int i = 0; i < prizesToDistribute; i++) {
            Prize prize = pendingPrizes.get(i);
            RankingDTO winningTeam = leaderboard.get(i);

            prize.setTeamId(winningTeam.getTeamId());
            prize.setStatus("DELIVERED");

            prizeRepository.save(prize);
        }
    }
}