package com.grupo18.ranking_service.services;

import com.grupo18.ranking_service.clients.TournamentClient;
import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.ranking_service.models.dtos.TournamentDTO;
import com.grupo18.ranking_service.repositories.RankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static reactor.netty.http.HttpConnectionLiveness.log;
import java.util.List;
import java.util.Optional;

@Service
public class RankingServiceImpl implements RankingService {



    private static final int POINTS_PER_WIN = 3;
    private static final int POINTS_PER_LOSS = 0;

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private TournamentClient tournamentClient;

    @Transactional
    @Override
    public void processMatchResult(MatchResultUpdateDTO updateDTO) {
        log.info("Procesando resultado de partido. Torneo ID: {}, Ganador ID: {}, Perdedor ID: {}",
                updateDTO.getTournamentId(), updateDTO.getWinnerTeamId(), updateDTO.getLoserTeamId());

        TournamentDTO tournament = tournamentClient.getTournamentById(updateDTO.getTournamentId());
        if (tournament == null || "FINISHED".equalsIgnoreCase(tournament.getState())) {
            log.warn("Torneo ID {} no permite actualización de ranking. Estado: {}",
                    updateDTO.getTournamentId(), tournament != null ? tournament.getState() : "null");
            throw new RuntimeException("No se pueden sumar puntos a un torneo finalizado o inexistente");
        }

        Ranking winnerRanking = rankingRepository
                .findByTournamentIdAndTeamId(updateDTO.getTournamentId(), updateDTO.getWinnerTeamId())
                .orElseGet(() -> {
                    log.info("Creando entrada de ranking para equipo ganador ID: {}", updateDTO.getWinnerTeamId());
                    return createNewRanking(updateDTO.getTournamentId(), updateDTO.getWinnerTeamId());
                });

        winnerRanking.setPoints(winnerRanking.getPoints() + POINTS_PER_WIN);
        winnerRanking.setWins(winnerRanking.getWins() + 1);
        winnerRanking.setMatchesPlayed(winnerRanking.getMatchesPlayed() + 1);
        rankingRepository.save(winnerRanking);
        log.info("Ranking actualizado para equipo ganador ID: {}. Puntos totales: {}, Victorias: {}",
                updateDTO.getWinnerTeamId(), winnerRanking.getPoints(), winnerRanking.getWins());

        Ranking loserRanking = rankingRepository
                .findByTournamentIdAndTeamId(updateDTO.getTournamentId(), updateDTO.getLoserTeamId())
                .orElseGet(() -> {
                    log.info("Creando entrada de ranking para equipo perdedor ID: {}", updateDTO.getLoserTeamId());
                    return createNewRanking(updateDTO.getTournamentId(), updateDTO.getLoserTeamId());
                });

        loserRanking.setPoints(loserRanking.getPoints() + POINTS_PER_LOSS);
        loserRanking.setLosses(loserRanking.getLosses() + 1);
        loserRanking.setMatchesPlayed(loserRanking.getMatchesPlayed() + 1);
        rankingRepository.save(loserRanking);
        log.info("Ranking actualizado para equipo perdedor ID: {}. Puntos totales: {}, Derrotas: {}",
                updateDTO.getLoserTeamId(), loserRanking.getPoints(), loserRanking.getLosses());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Ranking> getTournamentLeaderboard(Long tournamentId) {
        log.info("Obteniendo tabla de posiciones del torneo ID: {}", tournamentId);
        List<Ranking> tabla = rankingRepository.findByTournamentIdOrderByPointsDesc(tournamentId);
        log.info("Tabla de posiciones: {} equipo(s) en torneo ID: {}", tabla.size(), tournamentId);
        return tabla;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Ranking> findByTournamentAndTeam(Long tournamentId, Long teamId) {
        log.info("Buscando ranking del equipo ID: {} en torneo ID: {}", teamId, tournamentId);
        return rankingRepository.findByTournamentIdAndTeamId(tournamentId, teamId);
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
}