package com.grupo18.prize_service.services;

import com.grupo18.prize_service.clients.RankingClient;
import com.grupo18.prize_service.clients.TournamentClient;
import com.grupo18.prize_service.exceptions.PrizeException;
import com.grupo18.prize_service.models.Prize;
import com.grupo18.prize_service.models.dtos.RankingDTO;
import com.grupo18.prize_service.models.dtos.TournamentDTO;
import com.grupo18.prize_service.repositories.PrizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Creando premio de ${} para torneo ID: {}", prize.getAmount(), prize.getTournamentId());

        TournamentDTO tournament = tournamentClient.getTournamentById(prize.getTournamentId());
        if (tournament == null) {
            log.warn("Torneo ID {} no encontrado al crear premio", prize.getTournamentId());
            throw new PrizeException("El torneo indicado no existe");
        }

        Prize saved = prizeRepository.save(prize);
        log.info("Premio ID {} creado exitosamente. Monto: ${}, Estado: '{}'",
                saved.getPrizeId(), saved.getAmount(), saved.getStatus());
        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public Prize findById(Long id) {
        log.info("Buscando premio con ID: {}", id);
        return prizeRepository.findById(id).orElseThrow(() -> {
            log.warn("Premio con ID {} no encontrado", id);
            return new PrizeException("Premio no encontrado");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Prize> findAll() {
        log.info("Obteniendo listado completo de premios");
        List<Prize> lista = prizeRepository.findAll();
        log.info("Se encontraron {} premio(s) en total", lista.size());
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Prize> findByTournamentId(Long tournamentId) {
        log.info("Buscando premios del torneo ID: {}", tournamentId);
        List<Prize> lista = prizeRepository.findByTournamentId(tournamentId);
        log.info("Se encontraron {} premio(s) para torneo ID: {}", lista.size(), tournamentId);
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Prize> findByTeamId(Long teamId) {
        log.info("Buscando premios entregados al equipo ID: {}", teamId);
        List<Prize> lista = prizeRepository.findByTeamId(teamId);
        log.info("Se encontraron {} premio(s) para equipo ID: {}", lista.size(), teamId);
        return lista;
    }

    @Transactional
    @Override
    public void distributePrizesForTournament(Long tournamentId) {
        log.info("Iniciando distribución de premios para torneo ID: {}", tournamentId);

        TournamentDTO tournament = tournamentClient.getTournamentById(tournamentId);
        if (tournament == null || !"FINISHED".equalsIgnoreCase(tournament.getState())) {
            log.warn("No se puede distribuir premios. Torneo ID: {} - Estado: {}",
                    tournamentId, tournament != null ? tournament.getState() : "null");
            throw new PrizeException("Solo se pueden repartir premios cuando el torneo está en estado FINISHED");
        }

        List<Prize> pendingPrizes = prizeRepository.findByTournamentIdAndStatus(tournamentId, "PENDING");
        if (pendingPrizes.isEmpty()) {
            log.warn("No hay premios en estado PENDING para el torneo ID: {}", tournamentId);
            return;
        }
        log.info("Se encontraron {} premio(s) PENDING para distribuir en torneo ID: {}",
                pendingPrizes.size(), tournamentId);

        pendingPrizes.sort((p1, p2) -> Double.compare(p2.getAmount(), p1.getAmount()));

        List<RankingDTO> leaderboard = rankingClient.getTournamentLeaderboard(tournamentId);
        log.info("Tabla de posiciones obtenida: {} equipo(s) en el ranking", leaderboard.size());

        int prizesToDistribute = Math.min(pendingPrizes.size(), leaderboard.size());
        for (int i = 0; i < prizesToDistribute; i++) {
            Prize prize = pendingPrizes.get(i);
            RankingDTO winningTeam = leaderboard.get(i);

            prize.setTeamId(winningTeam.getTeamId());
            prize.setStatus("DELIVERED");
            prizeRepository.save(prize);

            log.info("Premio ID {} (${}) asignado al equipo ID {} (posición {})",
                    prize.getPrizeId(), prize.getAmount(), winningTeam.getTeamId(), i + 1);
        }

        log.info("Distribución completada. {} premio(s) entregados en torneo ID: {}",
                prizesToDistribute, tournamentId);
    }
}