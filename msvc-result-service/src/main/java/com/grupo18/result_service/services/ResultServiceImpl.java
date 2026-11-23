package com.grupo18.result_service.services;

import com.grupo18.result_service.clients.MatchClient;
import com.grupo18.result_service.clients.RankingClient;
import com.grupo18.result_service.exceptions.ResultException;
import com.grupo18.result_service.models.Result;
import com.grupo18.result_service.models.dtos.MatchDTO;
import com.grupo18.result_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.result_service.repositories.ResultRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ResultServiceImpl implements ResultService {

	@Autowired
	private ResultRepository resultRepository;

	@Autowired
	private MatchClient matchClient;

	@Autowired
	private RankingClient rankingClient;

	@Transactional(readOnly = true)
	@Override
	public List<Result> findAll() {
		log.info("Obteniendo listado completo de resultados");
		return resultRepository.findAll();
	}

	@Transactional
	@Override
	public synchronized Result save(Result result) {
		MatchDTO match = partida(result.getMatchId());
		if (resultRepository.existsByMatchId(result.getMatchId())) throw new ResultException(
			"Ya existe un resultado registrado para este partido"
		);
		validar(result, match);
		result.setResultId(null);
		result.setStatus("CONFIRMED");
		Result guardado = resultRepository.save(result);
		sincronizar(guardado, match, true);
		return guardado;
	}

	private MatchDTO partida(Long id) {
		MatchDTO match = matchClient.getMatchById(id);
		if (match == null) throw new ResultException("El partido reportado no existe");
		if ("CANCELLED".equals(match.getStatus())) throw new ResultException(
			"La partida está cancelada"
		);
		return match;
	}

	private void validar(Result r, MatchDTO m) {
		boolean individual = m.getHomeUserId() != null && m.getAwayUserId() != null;
		Long ganador = individual ? r.getWinnerUserId() : r.getWinnerTeamId();
		Long local = individual ? m.getHomeUserId() : m.getHomeTeamId();
		Long visita = individual ? m.getAwayUserId() : m.getAwayTeamId();
		if (
			ganador == null ||
			(individual ? r.getWinnerTeamId() != null : r.getWinnerUserId() != null) ||
			(!ganador.equals(local) && !ganador.equals(visita))
		) throw new ResultException(
			"El ganador debe ser uno de los equipos que jugó el partido o un jugador participante"
		);
		if (
			r.getHomeScore() == null ||
			r.getAwayScore() == null ||
			r.getHomeScore() < 0 ||
			r.getAwayScore() < 0 ||
			r.getHomeScore().equals(r.getAwayScore())
		) throw new ResultException("Ingresa marcadores no negativos y un ganador sin empate");
		Long ganadorMarcador = r.getHomeScore() > r.getAwayScore() ? local : visita;
		if (!ganador.equals(ganadorMarcador)) throw new ResultException(
			"El ganador no coincide con el marcador"
		);
	}

	private void sincronizar(Result r, MatchDTO m, boolean activo) {
		boolean individual = m.getHomeUserId() != null;
		Long ganador = individual ? r.getWinnerUserId() : r.getWinnerTeamId();
		Long local = individual ? m.getHomeUserId() : m.getHomeTeamId();
		Long visita = individual ? m.getAwayUserId() : m.getAwayTeamId();
		Long perdedor = ganador.equals(local) ? visita : local;
		var dto = new MatchResultUpdateDTO();
		dto.setMatchId(r.getMatchId());
		dto.setTournamentId(m.getTournamentId());
		dto.setActive(activo);
		if (individual) {
			dto.setWinnerUserId(ganador);
			dto.setLoserUserId(perdedor);
		} else {
			dto.setWinnerTeamId(ganador);
			dto.setLoserTeamId(perdedor);
		}
		rankingClient.updateRanking(dto);
		m.setStatus(activo ? "FINISHED" : "SCHEDULED");
		m.setHomeScore(activo ? r.getHomeScore() : null);
		m.setAwayScore(activo ? r.getAwayScore() : null);
		matchClient.actualizar(r.getMatchId(), m);
	}

	@Transactional(readOnly = true)
	@Override
	public Result findByMatchId(Long matchId) {
		log.info("Buscando resultado del partido ID: {}", matchId);
		return resultRepository.findByMatchId(matchId).orElseThrow(() -> {
			log.warn("No se encontró resultado para el partido ID: {}", matchId);
			return new ResultException("Resultado no encontrado para el partido");
		});
	}

	@Transactional(readOnly = true)
	@Override
	public List<Result> findByWinnerTeamId(Long winnerTeamId) {
		log.info("Buscando victorias del equipo ID: {}", winnerTeamId);
		List<Result> lista = resultRepository.findByWinnerTeamId(winnerTeamId);
		log.info("Se encontraron {} victoria(s) para equipo ID: {}", lista.size(), winnerTeamId);
		return lista;
	}

	@Transactional(readOnly = true)
	@Override
	public Result findById(Long id) {
		log.info("Buscando resultado con ID: {}", id);
		return resultRepository.findById(id).orElseThrow(() -> {
			log.warn("Resultado con ID: {} no encontrado", id);
			return new ResultException("Resultado no encontrado");
		});
	}

	@Transactional
	@Override
	public synchronized Result updateById(Long id, Result cambios) {
		Result actual = findById(id);
		MatchDTO match = partida(actual.getMatchId());
		validar(cambios, match);
		actual.setHomeScore(cambios.getHomeScore());
		actual.setAwayScore(cambios.getAwayScore());
		actual.setWinnerTeamId(cambios.getWinnerTeamId());
		actual.setWinnerUserId(cambios.getWinnerUserId());
		actual.setStatus("CONFIRMED");
		Result guardado = resultRepository.save(actual);
		sincronizar(guardado, match, true);
		return guardado;
	}

	@Transactional
	@Override
	public synchronized void deleteById(Long id) {
		Result actual = findById(id);
		MatchDTO match = partida(actual.getMatchId());
		actual.setStatus("ANULADO");
		resultRepository.save(actual);
		sincronizar(actual, match, false);
	}
}
