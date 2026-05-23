package com.grupo18.match_service.services;

import com.grupo18.match_service.clients.RegistrationClient;
import com.grupo18.match_service.clients.TournamentClient;
import com.grupo18.match_service.exceptions.MatchException; // Asegúrate de tener una clase para tus excepciones
import com.grupo18.match_service.models.Match;
import com.grupo18.match_service.models.dtos.TournamentDTO;
import com.grupo18.match_service.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TournamentClient tournamentClient;

    @Autowired
    private RegistrationClient registrationClient;

    @Transactional
    @Override
    public Match save(Match match) {
        // 1. Validar Torneo (Puerto 8003)
        TournamentDTO tournament = tournamentClient.getTournamentById(match.getTournamentId());
        if (tournament == null || !"UPCOMING".equalsIgnoreCase(tournament.getState()) && !"IN_PROGRESS".equalsIgnoreCase(tournament.getState())) {
            throw new MatchException("El torneo no existe o no permite nuevos partidos");
        }

        // 2. Validar inscripción del equipo Local (Puerto 8004)
        boolean isHomeRegistered = registrationClient.isTeamRegisteredInTournament(match.getHomeTeamId(), match.getTournamentId());
        if (!isHomeRegistered) {
            throw new MatchException("El equipo local no está inscrito en este torneo");
        }

        // 3. Validar inscripción del equipo Visitante (Puerto 8004)
        boolean isAwayRegistered = registrationClient.isTeamRegisteredInTournament(match.getAwayTeamId(), match.getTournamentId());
        if (!isAwayRegistered) {
            throw new MatchException("El equipo visitante no está inscrito en este torneo");
        }

        // 4. Regla: Un equipo no puede jugar contra sí mismo
        if (match.getHomeTeamId().equals(match.getAwayTeamId())) {
            throw new MatchException("Un equipo no puede jugar contra sí mismo");
        }

        match.setStatus("SCHEDULED"); // Estado inicial
        return matchRepository.save(match);
    }

    @Transactional(readOnly = true)
    @Override
    public Match findById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new MatchException("Partido no encontrado"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Match> findByTournamentId(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Match> findByTeamId(Long teamId) {
        return matchRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId);
    }
}