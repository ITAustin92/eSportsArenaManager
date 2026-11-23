package com.grupo18.match_service.services;

import com.grupo18.match_service.clients.RegistrationClient;
import com.grupo18.match_service.clients.TournamentClient;
import com.grupo18.match_service.exceptions.MatchException; // Asegúrate de tener una clase para tus excepciones
import com.grupo18.match_service.models.Match;
import com.grupo18.match_service.models.dtos.TournamentDTO;
import com.grupo18.match_service.repositories.MatchRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private TournamentClient tournamentClient;

	@Autowired
	private RegistrationClient registrationClient;

	@Transactional(readOnly = true)
	@Override
	public List<Match> findAll() {
		log.info("Obteniendo listado completo de partidos");
		List<Match> partidos = matchRepository.findAll();
		log.info("Se encontraron {} partido(s) en total", partidos.size());
		return partidos;
	}

	@Transactional
	@Override
	public Match save(Match match) {
		log.info(
			"Intentando crear partido. Torneo ID: {}, Equipo Local ID: {}, Equipo Visitante ID: {}",
			match.getTournamentId(),
			match.getHomeTeamId(),
			match.getAwayTeamId()
		);

		boolean individual =
			match.getHomeUserId() != null &&
			match.getAwayUserId() != null &&
			match.getHomeTeamId() == null &&
			match.getAwayTeamId() == null;
		boolean equipos =
			match.getHomeTeamId() != null &&
			match.getAwayTeamId() != null &&
			match.getHomeUserId() == null &&
			match.getAwayUserId() == null;
		if (!individual && !equipos) throw new MatchException("Selecciona dos jugadores o dos equipos");
		Long local = individual ? match.getHomeUserId() : match.getHomeTeamId();
		Long visita = individual ? match.getAwayUserId() : match.getAwayTeamId();
		if (local.equals(visita)) throw new MatchException(
			individual
				? "Un jugador no puede jugar contra sí mismo"
				: "Un equipo no puede jugar contra sí mismo"
		);
		TournamentDTO torneo = tournamentClient.getTournamentById(match.getTournamentId());
		if (
			torneo == null || !java.util.Set.of("UPCOMING", "IN_PROGRESS").contains(torneo.getState())
		) throw new MatchException("El torneo no existe o no permite nuevos partidos");
		boolean localInscrito = individual
			? registrationClient.isPlayerRegisteredInTournament(local, match.getTournamentId())
			: registrationClient.isTeamRegisteredInTournament(local, match.getTournamentId());
		boolean visitaInscrita = individual
			? registrationClient.isPlayerRegisteredInTournament(visita, match.getTournamentId())
			: registrationClient.isTeamRegisteredInTournament(visita, match.getTournamentId());
		if (!localInscrito) throw new MatchException("El equipo local no está inscrito en este torneo");
		if (!visitaInscrita) throw new MatchException(
			"El equipo visitante no está inscrito en este torneo"
		);
		match.setMatchId(null);
		match.setHomeScore(null);
		match.setAwayScore(null);
		match.setStatus("SCHEDULED");
		Match saved = matchRepository.save(match);
		log.info(
			"Partido creado exitosamente. ID: {}, Estado: '{}'",
			saved.getMatchId(),
			saved.getStatus()
		);
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
		existing.setRound(match.getRound());
		existing.setStatus(match.getStatus());
		existing.setHomeScore(match.getHomeScore());
		existing.setAwayScore(match.getAwayScore());
		Match updated = matchRepository.save(existing);
		log.info(
			"Partido ID {} actualizado. Nuevo estado: '{}'",
			updated.getMatchId(),
			updated.getStatus()
		);
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
