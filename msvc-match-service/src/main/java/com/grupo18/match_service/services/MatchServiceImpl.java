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

import static reactor.netty.http.HttpConnectionLiveness.log;

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
        log.info("Intentando crear partido. Torneo ID: {}, Equipo Local ID: {}, Equipo Visitante ID: {}",
                match.getTournamentId(), match.getHomeTeamId(), match.getAwayTeamId());

        if (match.getHomeTeamId().equals(match.getAwayTeamId())) {
            log.warn("Intento inválido: el equipo local y visitante son el mismo. ID: {}", match.getHomeTeamId());
            throw new MatchException("Un equipo no puede jugar contra sí mismo");
        }

        log.info("Validando torneo ID: {} contra tournament-service", match.getTournamentId());
        TournamentDTO tournament = tournamentClient.getTournamentById(match.getTournamentId());
        if (tournament == null || !"UPCOMING".equalsIgnoreCase(tournament.getState())
                && !"IN_PROGRESS".equalsIgnoreCase(tournament.getState())) {
            log.warn("Torneo ID {} no existe o no permite nuevos partidos. Estado: {}",
                    match.getTournamentId(), tournament != null ? tournament.getState() : "null");
            throw new MatchException("El torneo no existe o no permite nuevos partidos");
        }

        log.info("Validando inscripción del equipo local ID: {} en torneo ID: {}",
                match.getHomeTeamId(), match.getTournamentId());
        boolean isHomeRegistered = registrationClient.isTeamRegisteredInTournament(
                match.getHomeTeamId(), match.getTournamentId());
        if (!isHomeRegistered) {
            log.warn("Equipo local ID: {} no está inscrito en torneo ID: {}",
                    match.getHomeTeamId(), match.getTournamentId());
            throw new MatchException("El equipo local no está inscrito en este torneo");
        }


        log.info("Validando inscripción del equipo visitante ID: {} en torneo ID: {}",
                match.getAwayTeamId(), match.getTournamentId());
        boolean isAwayRegistered = registrationClient.isTeamRegisteredInTournament(
                match.getAwayTeamId(), match.getTournamentId());
        if (!isAwayRegistered) {
            log.warn("Equipo visitante ID: {} no está inscrito en torneo ID: {}",
                    match.getAwayTeamId(), match.getTournamentId());
            throw new MatchException("El equipo visitante no está inscrito en este torneo");
        }

        match.setStatus("SCHEDULED");
        Match saved = matchRepository.save(match);
        log.info("Partido creado exitosamente. ID: {}, Estado: '{}'", saved.getMatchId(), saved.getStatus());
        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public Match findById(Long id) {
        log.info("Buscando partida con ID: {}", id);
        return matchRepository.findById(id).orElseThrow(() -> {
            log.warn("Partida conID {} no encontrado", id);
            return new MatchException("Partido no encontrado");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Match> findByTournamentId(Long tournamentId) {
        log.info("Buscando partidas del torneo ID: {}", tournamentId);
        List<Match> partidos = matchRepository.findByTournamentId(tournamentId);
        log.info("Se encontraron {} partido(s) para el torneo ID: {}", partidos.size(), tournamentId);
        return partidos;
    }
    @Transactional(readOnly = true)
    @Override
    public List<Match> findByTeamId(Long teamId) {
        log.info("Buscando partidos del equipo ID: {}", teamId);
        List<Match> partidos = matchRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId);
        log.info("Se encontraron {} partido(s) para el equipo ID: {}", partidos.size(), teamId);
        return partidos;
    }

    @Transactional
    @Override
    public Match updateById(Long id, Match match) {
        log.info("Actualizando partido con ID: {}", id);
        Match existing = matchRepository.findById(id).orElseThrow(() -> {
            log.warn("Partido con ID {} no encontrado para actualizar", id);
            return new MatchException("Partido no encontrado");
        });
        existing.setMatchDate(match.getMatchDate());
        existing.setStatus(match.getStatus());
        existing.setHomeScore(match.getHomeScore());
        existing.setAwayScore(match.getAwayScore());
        Match updated = matchRepository.save(existing);
        log.info("Partido ID {} actualizado. Nuevo estado: '{}'", updated.getMatchId(), updated.getStatus());
        return updated;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Cancelando partido con ID: {}", id);
        Match existing = matchRepository.findById(id).orElseThrow(() -> {
            log.warn("Partido con ID {} no encontrado para cancelar", id);
            return new MatchException("Partido no encontrado");
        });
        existing.setStatus("CANCELLED");
        matchRepository.save(existing);
        log.info("Partido ID {} cancelado exitosamente", id);
    }
}