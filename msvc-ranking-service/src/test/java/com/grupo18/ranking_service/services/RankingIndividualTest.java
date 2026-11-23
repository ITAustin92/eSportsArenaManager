package com.grupo18.ranking_service.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.grupo18.ranking_service.clients.TournamentClient;
import com.grupo18.ranking_service.models.*;
import com.grupo18.ranking_service.models.dtos.*;
import com.grupo18.ranking_service.repositories.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankingIndividualTest {

	@org.mockito.Mock
	org.springframework.transaction.PlatformTransactionManager transacciones;

	@org.junit.jupiter.api.BeforeEach
	void prepararTransaccion() {
		org.mockito.Mockito.lenient()
			.when(transacciones.getTransaction(org.mockito.ArgumentMatchers.any()))
			.thenAnswer(i -> new org.springframework.transaction.support.SimpleTransactionStatus());
	}

	@Mock
	RankingRepository rankingRepository;

	@Mock
	MatchContributionRepository contributions;

	@Mock
	TournamentClient tournamentClient;

	@InjectMocks
	RankingServiceImpl servicio;

	Map<Long, Ranking> filas = new HashMap<>();
	Map<Long, MatchContribution> aportes = new HashMap<>();

	@BeforeEach
	void preparar() {
		var torneo = new TournamentDTO();
		torneo.setState("IN_PROGRESS");
		lenient().when(tournamentClient.getTournamentById(1L)).thenReturn(torneo);
		lenient()
			.when(contributions.findById(anyLong()))
			.thenAnswer(i -> Optional.ofNullable(aportes.get(i.getArgument(0))));
		lenient()
			.when(contributions.save(any()))
			.thenAnswer(i -> {
				MatchContribution a = i.getArgument(0);
				aportes.put(a.getMatchId(), a);
				return a;
			});
		lenient()
			.when(rankingRepository.findByTournamentIdAndUserId(eq(1L), anyLong()))
			.thenAnswer(i -> Optional.ofNullable(filas.get(i.getArgument(1))));
		lenient()
			.when(rankingRepository.save(any()))
			.thenAnswer(i -> {
				Ranking r = i.getArgument(0);
				filas.put(r.getUserId(), r);
				return r;
			});
	}

	MatchResultUpdateDTO dto(Long ganador, Long perdedor) {
		var d = new MatchResultUpdateDTO();
		d.setMatchId(10L);
		d.setTournamentId(1L);
		d.setWinnerUserId(ganador);
		d.setLoserUserId(perdedor);
		return d;
	}

	@Test
	void repetirResultadoNoDuplicaPuntos() {
		servicio.processMatchResult(dto(2L, 3L));
		servicio.processMatchResult(dto(2L, 3L));
		assertThat(filas.get(2L).getPoints()).isEqualTo(3);
		assertThat(filas.get(2L).getMatchesPlayed()).isEqualTo(1);
		assertThat(filas.get(3L).getLosses()).isEqualTo(1);
	}

	@Test
	void corregirGanadorReemplazaSuAporte() {
		servicio.processMatchResult(dto(2L, 3L));
		servicio.processMatchResult(dto(3L, 2L));
		assertThat(filas.get(2L).getPoints()).isZero();
		assertThat(filas.get(2L).getLosses()).isEqualTo(1);
		assertThat(filas.get(3L).getPoints()).isEqualTo(3);
		assertThat(filas.get(3L).getMatchesPlayed()).isEqualTo(1);
	}

	@Test
	void anularDosVecesRetiraLosPuntosUnaSolaVez() {
		servicio.processMatchResult(dto(2L, 3L));
		var anulacion = dto(2L, 3L);
		anulacion.setActive(false);
		servicio.processMatchResult(anulacion);
		servicio.processMatchResult(anulacion);
		assertThat(filas.get(2L).getPoints()).isZero();
		assertThat(filas.get(2L).getMatchesPlayed()).isZero();
		assertThat(filas.get(3L).getLosses()).isZero();
	}
}
