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
        TournamentDTO tournament = tournamentClient.getTournamentById(match.getTournamentId());
        if (tournament == null || !"UPCOMING".equalsIgnoreCase(tournament.getState()) && !"IN_PROGRESS".equalsIgnoreCase(tournament.getState())) {
            throw new MatchException("El torneo no existe o no permite nuevos partidos");
        }

        boolean isHomeRegistered = registrationClient.isTeamRegisteredInTournament(match.getHomeTeamId(), match.getTournamentId());
        if (!isHomeRegistered) {
            throw new MatchException("El equipo local no está inscrito en este torneo");
        }

        boolean isAwayRegistered = registrationClient.isTeamRegisteredInTournament(match.getAwayTeamId(), match.getTournamentId());
        if (!isAwayRegistered) {
            throw new MatchException("El equipo visitante no está inscrito en este torneo");
        }

        if (match.getHomeTeamId().equals(match.getAwayTeamId())) {
            throw new MatchException("Un equipo no puede jugar contra sí mismo");
        }

        match.setStatus("SCHEDULED");
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

    @Transactional
    @Override
    public Match updateById(Long id, Match match) {
        Match existing = matchRepository.findById(id)
                .orElseThrow(() -> new MatchException("Partido no encontrado"));
        existing.setMatchDate(match.getMatchDate());
        existing.setStatus(match.getStatus());
        existing.setHomeScore(match.getHomeScore());
        existing.setAwayScore(match.getAwayScore());
        return matchRepository.save(existing);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Match existing = matchRepository.findById(id)
                .orElseThrow(() -> new MatchException("Partido no encontrado"));
        existing.setStatus("CANCELLED");
        matchRepository.save(existing);
    }
}