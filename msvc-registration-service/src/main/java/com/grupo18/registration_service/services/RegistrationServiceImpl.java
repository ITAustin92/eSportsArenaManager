package com.grupo18.registration_service.services;

import com.grupo18.registration_service.clients.*;
import com.grupo18.registration_service.models.Registration;
import com.grupo18.registration_service.models.dtos.*;
import com.grupo18.registration_service.repositories.RegistrationRepository;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	private static final ZoneId ZONA = ZoneId.of("America/Santiago");
	private static final Set<String> LIBERADOS = Set.of("CANCELLED", "REJECTED");
	private static final Set<String> ESTADOS = Set.of(
		"PENDING",
		"ACCEPTED",
		"CONFIRMED",
		"REJECTED",
		"CANCELLED"
	);

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private TeamClient teamClient;

	@Autowired
	private TournamentClient tournamentClient;

	@Autowired
	private GameClient gameClient;

	@Autowired
	private UserClient userClient;

	@Autowired
	private SanctionClient sanctionClient;

	@Autowired
	private PlatformTransactionManager transacciones;

	@Autowired
	private JdbcTemplate jdbc;

	@Value("${spring.datasource.url:}")
	private String jdbcUrl;

	// El monitor cubre también el COMMIT en H2. PostgreSQL agrega un bloqueo por
	// torneo para respetar los cupos incluso cuando hay varias instancias del servicio.
	@Override
	public synchronized Registration save(Registration solicitud) {
		return new TransactionTemplate(transacciones).execute(tx -> {
			bloquearTorneo(solicitud.getTournamentId());
			exigirParticipante(solicitud);
			Optional<Registration> anterior =
				solicitud.getTeamId() != null
					? registrationRepository.findByTeamIdAndTournamentId(
							solicitud.getTeamId(),
							solicitud.getTournamentId()
						)
					: registrationRepository.findByUserIdAndTournamentId(
							solicitud.getUserId(),
							solicitud.getTournamentId()
						);
			if (anterior.isPresent() && !LIBERADOS.contains(anterior.get().getStatus())) {
				throw error(HttpStatus.CONFLICT, "El participante ya está inscrito en este torneo");
			}
			validarReglas(solicitud, null);
			Registration registro = anterior.orElseGet(Registration::new);
			registro.setTeamId(solicitud.getTeamId());
			registro.setUserId(solicitud.getUserId());
			registro.setTournamentId(solicitud.getTournamentId());
			registro.setRegistrationDate(LocalDate.now(ZONA));
			registro.setStatus("PENDING");
			return registrationRepository.saveAndFlush(registro);
		});
	}

	private void bloquearTorneo(Long id) {
		if (id == null) throw error(HttpStatus.BAD_REQUEST, "Selecciona un torneo");
		if (jdbcUrl.startsWith("jdbc:postgresql:")) {
			jdbc.queryForList("select pg_advisory_xact_lock(?)", id);
		}
	}

	private void exigirParticipante(Registration r) {
		if ((r.getTeamId() == null) == (r.getUserId() == null)) {
			throw error(HttpStatus.BAD_REQUEST, "Selecciona un equipo o un jugador, nunca ambos");
		}
	}

	private void validarReglas(Registration r, Long excluirId) {
		TournamentDTO torneo = tournamentClient.getTournamentById(r.getTournamentId());
		Instant cierre =
			torneo.getRegistrationDeadline() != null
				? torneo.getRegistrationDeadline()
				: torneo.getStartDate().atStartOfDay(ZONA).toInstant();
		if (!"UPCOMING".equals(torneo.getState()) || !Instant.now().isBefore(cierre)) {
			throw error(HttpStatus.CONFLICT, "El torneo no admite inscripciones o su plazo terminó");
		}
		long ocupados = registrationRepository
			.findByTournamentId(torneo.getTournamentId())
			.stream()
			.filter(i -> !Objects.equals(i.getRegistrationId(), excluirId))
			.filter(i -> !LIBERADOS.contains(i.getStatus()))
			.count();
		int maximo = torneo.getMaxParticipants() == null ? 16 : torneo.getMaxParticipants();
		if (ocupados >= maximo) throw error(HttpStatus.CONFLICT, "No quedan cupos disponibles");

		GameDTO juego = gameClient.obtener(torneo.getGameId());
		if (juego == null || !"ACTIVO".equals(juego.getEstado())) {
			throw error(HttpStatus.CONFLICT, "El juego no está disponible");
		}
		if (r.getUserId() != null) {
			if (!Integer.valueOf(1).equals(juego.getJugadoresPorEquipo())) {
				throw error(HttpStatus.BAD_REQUEST, "Este torneo requiere un equipo completo");
			}
			validarJugador(r.getUserId(), torneo.getTournamentId());
			return;
		}
		if (Integer.valueOf(1).equals(juego.getJugadoresPorEquipo())) {
			throw error(HttpStatus.BAD_REQUEST, "Los torneos 1v1 requieren inscripción individual");
		}
		TeamDTO equipo = teamClient.getTeamById(r.getTeamId());
		if (equipo == null || !"ACTIVO".equals(equipo.getEstado())) {
			throw error(HttpStatus.CONFLICT, "El equipo no está activo");
		}
		if (!Objects.equals(equipo.getJuegoPrincipalId(), torneo.getGameId())) {
			throw error(HttpStatus.BAD_REQUEST, "El equipo debe competir en el mismo juego del torneo");
		}
		if (
			equipo.getMiembros() == null || equipo.getMiembros().size() != juego.getJugadoresPorEquipo()
		) {
			throw error(HttpStatus.CONFLICT, "El equipo no tiene la cantidad de integrantes requerida");
		}
		if (
			equipo.getMiembros().stream().map(TeamDTO.MemberDTO::getUsuarioId).distinct().count() !=
			equipo.getMiembros().size()
		) {
			throw error(HttpStatus.BAD_REQUEST, "El equipo tiene integrantes repetidos");
		}
		if (bloqueado(sanctionClient.porEquipo(r.getTeamId()), torneo.getTournamentId())) {
			throw error(HttpStatus.FORBIDDEN, "El equipo tiene una sanción vigente en este torneo");
		}
		for (var miembro : equipo.getMiembros())
			validarJugador(miembro.getUsuarioId(), torneo.getTournamentId());
	}

	private void validarJugador(Long id, Long torneoId) {
		UserDTO jugador = userClient.obtener(id);
		if (jugador == null || !"ACTIVO".equals(jugador.getEstado())) {
			throw error(HttpStatus.FORBIDDEN, "El jugador no está activo para competir");
		}
		if (bloqueado(sanctionClient.porUsuario(id), torneoId)) {
			throw error(HttpStatus.FORBIDDEN, "Un participante tiene una sanción vigente en este torneo");
		}
	}

	private boolean bloqueado(List<SanctionDTO> sanciones, Long torneoId) {
		return sanciones
			.stream()
			.anyMatch(
				s ->
					"ACTIVE".equals(s.getStatus()) &&
					(s.getTournamentId() == null || Objects.equals(s.getTournamentId(), torneoId))
			);
	}

	@Override
	public List<Registration> findAll() {
		return registrationRepository.findAll();
	}

	@Override
	public Registration findById(Long id) {
		return registrationRepository
			.findById(id)
			.orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
	}

	@Override
	public List<Registration> findByTeamId(Long id) {
		return registrationRepository.findByTeamId(id);
	}

	@Override
	public List<Registration> findByTournamentId(Long id) {
		return registrationRepository.findByTournamentId(id);
	}

	@Override
	public synchronized Registration updateStatus(Long id, String estado) {
		if (!ESTADOS.contains(estado)) throw error(
			HttpStatus.BAD_REQUEST,
			"Estado de inscripción inválido"
		);
		return new TransactionTemplate(transacciones).execute(tx -> {
			Registration registro = findById(id);
			bloquearTorneo(registro.getTournamentId());
			if (!LIBERADOS.contains(estado)) validarReglas(registro, registro.getRegistrationId());
			registro.setStatus(estado);
			return registrationRepository.saveAndFlush(registro);
		});
	}

	@Override
	public void cancelById(Long id) {
		updateStatus(id, "CANCELLED");
	}

	@Override
	public boolean existsByTeamIdAndTournamentId(Long equipoId, Long torneoId) {
		return registrationRepository
			.findByTeamIdAndTournamentId(equipoId, torneoId)
			.filter(i -> !LIBERADOS.contains(i.getStatus()))
			.isPresent();
	}

	private ResponseStatusException error(HttpStatus estado, String mensaje) {
		return new ResponseStatusException(estado, mensaje);
	}
}
