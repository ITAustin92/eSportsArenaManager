package com.grupo18.registration_service.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.grupo18.registration_service.clients.*;
import com.grupo18.registration_service.models.Registration;
import com.grupo18.registration_service.models.dtos.*;
import com.grupo18.registration_service.repositories.RegistrationRepository;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

	@Mock
	RegistrationRepository registrationRepository;

	@Mock
	TeamClient teamClient;

	@Mock
	TournamentClient tournamentClient;

	@Mock
	GameClient gameClient;

	@Mock
	UserClient userClient;

	@Mock
	SanctionClient sanctionClient;

	@Mock
	PlatformTransactionManager transacciones;

	@InjectMocks
	RegistrationServiceImpl servicio;

	Registration solicitud;
	TournamentDTO torneo;
	TeamDTO equipo;
	GameDTO juego;

	@BeforeEach
	void preparar() {
		ReflectionTestUtils.setField(servicio, "jdbcUrl", "jdbc:h2:mem:pruebas");
		lenient()
			.when(transacciones.getTransaction(any()))
			.thenAnswer(i -> new SimpleTransactionStatus());
		lenient()
			.when(registrationRepository.saveAndFlush(any()))
			.thenAnswer(i -> i.getArgument(0));
		solicitud = new Registration();
		solicitud.setTeamId(100L);
		solicitud.setTournamentId(10L);
		solicitud.setStatus("PENDING");
		torneo = new TournamentDTO();
		torneo.setTournamentId(10L);
		torneo.setGameId(1L);
		torneo.setState("UPCOMING");
		torneo.setStartDate(LocalDate.now().plusDays(2));
		torneo.setRegistrationDeadline(Instant.now().plusSeconds(3600));
		torneo.setMaxParticipants(2);
		juego = new GameDTO();
		juego.setEstado("ACTIVO");
		juego.setJugadoresPorEquipo(2);
		equipo = new TeamDTO();
		equipo.setId(100L);
		equipo.setEstado("ACTIVO");
		equipo.setJuegoPrincipalId(1L);
		var uno = new TeamDTO.MemberDTO();
		uno.setUsuarioId(2L);
		var dos = new TeamDTO.MemberDTO();
		dos.setUsuarioId(3L);
		equipo.setMiembros(List.of(uno, dos));
		lenient().when(tournamentClient.getTournamentById(10L)).thenReturn(torneo);
		lenient().when(gameClient.obtener(1L)).thenReturn(juego);
		lenient().when(teamClient.getTeamById(100L)).thenReturn(equipo);
		var jugador = new UserDTO();
		jugador.setEstado("ACTIVO");
		lenient().when(userClient.obtener(anyLong())).thenReturn(jugador);
	}

	private void rechaza(HttpStatus estado, Runnable accion) {
		assertThatThrownBy(accion::run).isInstanceOfSatisfying(ResponseStatusException.class, fallo ->
			assertThat(fallo.getStatusCode()).isEqualTo(estado)
		);
		verify(registrationRepository, never()).saveAndFlush(any());
	}

	@Test
	void creaEquipoValidoConIdDelServidor() {
		solicitud.setRegistrationId(999L);
		var creado = servicio.save(solicitud);
		assertThat(creado.getRegistrationId()).isNull();
		assertThat(creado.getStatus()).isEqualTo("PENDING");
		assertThat(creado.getRegistrationDate()).isNotNull();
		verify(transacciones).commit(any());
	}

	@Test
	void creaInscripcionIndividual() {
		solicitud.setTeamId(null);
		solicitud.setUserId(2L);
		juego.setJugadoresPorEquipo(1);
		assertThat(servicio.save(solicitud).getUserId()).isEqualTo(2L);
		verifyNoInteractions(teamClient);
	}

	@Test
	void rechazaParticipanteDuplicado() {
		when(registrationRepository.findByTeamIdAndTournamentId(100L, 10L)).thenReturn(
			Optional.of(solicitud)
		);
		rechaza(HttpStatus.CONFLICT, () -> servicio.save(solicitud));
		verify(transacciones).rollback(any());
	}

	@Test
	void permiteReinscribirCanceladoSinDuplicarFila() {
		var anterior = new Registration();
		anterior.setRegistrationId(8L);
		anterior.setStatus("CANCELLED");
		when(registrationRepository.findByTeamIdAndTournamentId(100L, 10L)).thenReturn(
			Optional.of(anterior)
		);
		assertThat(servicio.save(solicitud).getRegistrationId()).isEqualTo(8L);
		assertThat(anterior.getStatus()).isEqualTo("PENDING");
	}

	@Test
	void rechazaEquipoInactivo() {
		equipo.setEstado("INACTIVO");
		rechaza(HttpStatus.CONFLICT, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaTorneoFinalizado() {
		torneo.setState("FINISHED");
		rechaza(HttpStatus.CONFLICT, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaPlazoVencido() {
		torneo.setRegistrationDeadline(Instant.now().minusSeconds(1));
		rechaza(HttpStatus.CONFLICT, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaCuposLlenos() {
		var uno = new Registration();
		uno.setRegistrationId(1L);
		uno.setStatus("PENDING");
		var dos = new Registration();
		dos.setRegistrationId(2L);
		dos.setStatus("ACCEPTED");
		when(registrationRepository.findByTournamentId(10L)).thenReturn(List.of(uno, dos));
		rechaza(HttpStatus.CONFLICT, () -> servicio.save(solicitud));
	}

	@Test
	void canceladosNoOcupanCupo() {
		var cancelado = new Registration();
		cancelado.setRegistrationId(4L);
		cancelado.setStatus("CANCELLED");
		torneo.setMaxParticipants(1);
		when(registrationRepository.findByTournamentId(10L)).thenReturn(List.of(cancelado));
		assertThat(servicio.save(solicitud).getStatus()).isEqualTo("PENDING");
	}

	@Test
	void rechazaJuegoDistinto() {
		equipo.setJuegoPrincipalId(9L);
		rechaza(HttpStatus.BAD_REQUEST, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaEquipoIncompleto() {
		equipo.setMiembros(equipo.getMiembros().subList(0, 1));
		rechaza(HttpStatus.CONFLICT, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaIntegrantesDuplicados() {
		equipo.setMiembros(List.of(equipo.getMiembros().get(0), equipo.getMiembros().get(0)));
		rechaza(HttpStatus.BAD_REQUEST, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaSancionVigente() {
		var sancion = new SanctionDTO();
		sancion.setStatus("ACTIVE");
		sancion.setTournamentId(10L);
		when(sanctionClient.porUsuario(2L)).thenReturn(List.of(sancion));
		rechaza(HttpStatus.FORBIDDEN, () -> servicio.save(solicitud));
	}

	@Test
	void sancionDeOtroTorneoNoBloquea() {
		var sancion = new SanctionDTO();
		sancion.setStatus("ACTIVE");
		sancion.setTournamentId(99L);
		when(sanctionClient.porEquipo(100L)).thenReturn(List.of(sancion));
		assertThat(servicio.save(solicitud).getStatus()).isEqualTo("PENDING");
	}

	@Test
	void rechazaDosParticipantesEnUnaSolicitud() {
		solicitud.setUserId(2L);
		rechaza(HttpStatus.BAD_REQUEST, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaSolicitudSinParticipante() {
		solicitud.setTeamId(null);
		rechaza(HttpStatus.BAD_REQUEST, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaEquipoEnTorneoIndividual() {
		juego.setJugadoresPorEquipo(1);
		rechaza(HttpStatus.BAD_REQUEST, () -> servicio.save(solicitud));
	}

	@Test
	void rechazaJugadorSoloEnTorneoPorEquipos() {
		solicitud.setTeamId(null);
		solicitud.setUserId(2L);
		rechaza(HttpStatus.BAD_REQUEST, () -> servicio.save(solicitud));
	}

	@Test
	void confirmaExcluyendoSuPropioCupo() {
		solicitud.setRegistrationId(1L);
		torneo.setMaxParticipants(1);
		when(registrationRepository.findById(1L)).thenReturn(Optional.of(solicitud));
		when(registrationRepository.findByTournamentId(10L)).thenReturn(List.of(solicitud));
		assertThat(servicio.updateStatus(1L, "ACCEPTED").getStatus()).isEqualTo("ACCEPTED");
	}

	@Test
	void cancelaAunqueHayaCerradoElTorneo() {
		when(registrationRepository.findById(1L)).thenReturn(Optional.of(solicitud));
		servicio.cancelById(1L);
		assertThat(solicitud.getStatus()).isEqualTo("CANCELLED");
		verifyNoInteractions(tournamentClient);
	}

	@Test
	void actualizarInexistenteDevuelve404() {
		rechaza(HttpStatus.NOT_FOUND, () -> servicio.updateStatus(9999L, "ACCEPTED"));
	}

	@Test
	void rechazaEstadoDesconocido() {
		rechaza(HttpStatus.BAD_REQUEST, () -> servicio.updateStatus(1L, "OTRO"));
	}

	@Test
	void distingueInscripcionCanceladaDeVigente() {
		when(registrationRepository.findByTeamIdAndTournamentId(100L, 10L)).thenReturn(
			Optional.of(solicitud)
		);
		assertThat(servicio.existsByTeamIdAndTournamentId(100L, 10L)).isTrue();
		solicitud.setStatus("CANCELLED");
		assertThat(servicio.existsByTeamIdAndTournamentId(100L, 10L)).isFalse();
	}

	@Test
	void delegaConsultasAlRepositorio() {
		when(registrationRepository.findAll()).thenReturn(List.of(solicitud));
		when(registrationRepository.findByTeamId(100L)).thenReturn(List.of(solicitud));
		when(registrationRepository.findByTournamentId(10L)).thenReturn(List.of(solicitud));
		assertThat(servicio.findAll()).containsExactly(solicitud);
		assertThat(servicio.findByTeamId(100L)).containsExactly(solicitud);
		assertThat(servicio.findByTournamentId(10L)).containsExactly(solicitud);
	}
}
