package com.grupo18.ranking_service.services;

import com.grupo18.ranking_service.clients.TournamentClient;
import com.grupo18.ranking_service.exceptions.RankingException;
import com.grupo18.ranking_service.models.MatchContribution;
import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.ranking_service.models.dtos.TournamentDTO;
import com.grupo18.ranking_service.repositories.MatchContributionRepository;
import com.grupo18.ranking_service.repositories.RankingRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class RankingServiceImpl implements RankingService {

	private static final int POINTS_PER_WIN = 3;

	@Autowired
	private RankingRepository rankingRepository;

	@Autowired
	private TournamentClient tournamentClient;

	@Autowired
	private MatchContributionRepository contributions;

	@Autowired
	private PlatformTransactionManager transacciones;

	@Autowired
	private JdbcTemplate jdbc;

	@Value("${spring.datasource.url:}")
	private String jdbcUrl;

	// En H2 el monitor incluye el COMMIT. PostgreSQL agrega un bloqueo por torneo
	// para que dos instancias no reemplacen el mismo aporte simultáneamente.
	@Override
	public synchronized void processMatchResult(MatchResultUpdateDTO dto) {
		new TransactionTemplate(transacciones).executeWithoutResult(tx -> {
			if (
				jdbcUrl != null && jdbcUrl.startsWith("jdbc:postgresql:") && dto.getTournamentId() != null
			) {
				jdbc.queryForList(
					"select pg_advisory_xact_lock(?)",
					dto.getTournamentId() ^ 0x52414E4B00000000L
				);
			}
			aplicarResultado(dto);
		});
	}

	private void aplicarResultado(MatchResultUpdateDTO dto) {
		if (dto.getMatchId() == null) throw new RankingException("La partida es obligatoria");
		TournamentDTO torneo = tournamentClient.getTournamentById(dto.getTournamentId());
		if (torneo == null || "CANCELLED".equals(torneo.getState())) throw new RankingException(
			"No se pueden sumar puntos a un torneo inexistente o cancelado"
		);
		boolean individual =
			dto.getWinnerUserId() != null &&
			dto.getLoserUserId() != null &&
			dto.getWinnerTeamId() == null &&
			dto.getLoserTeamId() == null;
		boolean equipos =
			dto.getWinnerTeamId() != null &&
			dto.getLoserTeamId() != null &&
			dto.getWinnerUserId() == null &&
			dto.getLoserUserId() == null;
		if (!individual && !equipos) throw new RankingException(
			"Selecciona dos jugadores o dos equipos"
		);
		Long ganador = individual ? dto.getWinnerUserId() : dto.getWinnerTeamId();
		Long perdedor = individual ? dto.getLoserUserId() : dto.getLoserTeamId();
		if (ganador.equals(perdedor)) throw new RankingException(
			"Los participantes deben ser distintos"
		);
		var anterior = contributions.findById(dto.getMatchId()).orElse(null);
		if (anterior != null) {
			if (!anterior.getTournamentId().equals(dto.getTournamentId())) throw new RankingException(
				"No se puede cambiar el torneo de una partida"
			);
			if (anterior.isActive()) {
				ajustar(
					anterior.getTournamentId(),
					anterior.getWinnerTeamId(),
					anterior.getWinnerUserId(),
					true,
					-1
				);
				ajustar(
					anterior.getTournamentId(),
					anterior.getLoserTeamId(),
					anterior.getLoserUserId(),
					false,
					-1
				);
			}
		}
		if (dto.isActive()) {
			ajustar(dto.getTournamentId(), dto.getWinnerTeamId(), dto.getWinnerUserId(), true, 1);
			ajustar(dto.getTournamentId(), dto.getLoserTeamId(), dto.getLoserUserId(), false, 1);
		}
		var aporte = new MatchContribution();
		aporte.setMatchId(dto.getMatchId());
		aporte.setTournamentId(dto.getTournamentId());
		aporte.setWinnerTeamId(dto.getWinnerTeamId());
		aporte.setLoserTeamId(dto.getLoserTeamId());
		aporte.setWinnerUserId(dto.getWinnerUserId());
		aporte.setLoserUserId(dto.getLoserUserId());
		aporte.setActive(dto.isActive());
		contributions.save(aporte);
	}

	private void ajustar(Long torneo, Long equipo, Long usuario, boolean gana, int signo) {
		var fila = (
			usuario == null
				? rankingRepository.findByTournamentIdAndTeamId(torneo, equipo)
				: rankingRepository.findByTournamentIdAndUserId(torneo, usuario)
		).orElseGet(() -> {
			var nueva = new Ranking();
			nueva.setTournamentId(torneo);
			nueva.setTeamId(equipo);
			nueva.setUserId(usuario);
			return nueva;
		});
		fila.setMatchesPlayed(fila.getMatchesPlayed() + signo);
		fila.setWins(fila.getWins() + (gana ? signo : 0));
		fila.setLosses(fila.getLosses() + (gana ? 0 : signo));
		fila.setPoints(fila.getWins() * POINTS_PER_WIN);
		rankingRepository.save(fila);
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
}
