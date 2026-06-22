package com.grupo18.ranking_service.services;

import com.grupo18.ranking_service.clients.TournamentClient;
import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.ranking_service.models.dtos.TournamentDTO;
import com.grupo18.ranking_service.repositories.RankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private TournamentClient tournamentClient;

    private static final int POINTS_PER_WIN = 3;
    private static final int POINTS_PER_LOSS = 0;
    @Transactional
    @Override
    public void processMatchResult(MatchResultUpdateDTO updateDTO) {

        TournamentDTO tournament = tournamentClient.getTournamentById(updateDTO.getTournamentId());
        if (tournament == null || "FINISHED".equalsIgnoreCase(tournament.getState())) {
            throw new RuntimeException("No se pueden sumar puntos a un torneo finalizado o inexistente");
        }

        Ranking winnerRanking = rankingRepository.findByTournamentIdAndTeamId(
                updateDTO.getTournamentId(), updateDTO.getWinnerTeamId()
        ).orElse(createNewRanking(updateDTO.getTournamentId(), updateDTO.getWinnerTeamId()));

        winnerRanking.setPoints(winnerRanking.getPoints() + POINTS_PER_WIN); // Suma 3 puntos
        winnerRanking.setWins(winnerRanking.getWins() + 1);                  // Suma 1 victoria
        winnerRanking.setMatchesPlayed(winnerRanking.getMatchesPlayed() + 1); // Suma 1 partido jugado
        rankingRepository.save(winnerRanking);


        Ranking loserRanking = rankingRepository.findByTournamentIdAndTeamId(
                updateDTO.getTournamentId(), updateDTO.getLoserTeamId()
        ).orElse(createNewRanking(updateDTO.getTournamentId(), updateDTO.getLoserTeamId()));

        loserRanking.setPoints(loserRanking.getPoints() + POINTS_PER_LOSS); // Suma 0 puntos
        loserRanking.setLosses(loserRanking.getLosses() + 1);               // Suma 1 derrota
        loserRanking.setMatchesPlayed(loserRanking.getMatchesPlayed() + 1); // Suma 1 partido jugado
        rankingRepository.save(loserRanking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Ranking> getTournamentLeaderboard(Long tournamentId) {
        return rankingRepository.findByTournamentIdOrderByPointsDesc(tournamentId);
    }

    private Ranking createNewRanking(Long tournamentId, Long teamId) {
        Ranking newRanking = new Ranking();
        newRanking.setTournamentId(tournamentId);
        newRanking.setTeamId(teamId);
        newRanking.setPoints(0);
        newRanking.setWins(0);
        newRanking.setLosses(0);
        newRanking.setMatchesPlayed(0);
        return newRanking;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Ranking> findByTournamentAndTeam(Long tournamentId, Long teamId) {
        return rankingRepository.findByTournamentIdAndTeamId(tournamentId, teamId);
    }

}