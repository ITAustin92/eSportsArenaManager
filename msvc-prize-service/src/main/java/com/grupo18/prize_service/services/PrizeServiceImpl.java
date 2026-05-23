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
        // Regla de Negocio: No podemos crear un premio para un torneo que no existe
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

    // --- MAGIA DE LA REGLA DE NEGOCIO ---

    @Transactional
    @Override
    public void distributePrizesForTournament(Long tournamentId) {
        // 1. Validar que el torneo exista y esté finalizado
        TournamentDTO tournament = tournamentClient.getTournamentById(tournamentId);
        if (tournament == null || !"FINISHED".equalsIgnoreCase(tournament.getState())) {
            throw new RuntimeException("Solo se pueden repartir premios cuando el torneo está en estado FINISHED.");
        }

        // 2. Buscar los premios que están esperando dueño
        List<Prize> pendingPrizes = prizeRepository.findByTournamentIdAndStatus(tournamentId, "PENDING");
        if (pendingPrizes.isEmpty()) {
            return; // No hay premios para repartir, salimos sin error
        }

        // 3. Ordenar los premios por monto (de mayor a menor) para dar el más grande al primer lugar
        pendingPrizes.sort((p1, p2) -> Double.compare(p2.getAmount(), p1.getAmount()));

        // 4. Traer la tabla de posiciones desde el ranking-service
        // (Viene ya ordenada del 1er lugar hacia abajo gracias a nuestra consulta en su repositorio)
        List<RankingDTO> leaderboard = rankingClient.getTournamentLeaderboard(tournamentId);

        // 5. Repartir los premios uno a uno
        // Usamos Math.min por si hay más premios que equipos, o más equipos que premios
        int prizesToDistribute = Math.min(pendingPrizes.size(), leaderboard.size());

        for (int i = 0; i < prizesToDistribute; i++) {
            Prize prize = pendingPrizes.get(i);
            RankingDTO winningTeam = leaderboard.get(i);

            // Asignamos el equipo ganador al premio y cambiamos el estado
            prize.setTeamId(winningTeam.getTeamId());
            prize.setStatus("DELIVERED");

            prizeRepository.save(prize);
        }
    }
}