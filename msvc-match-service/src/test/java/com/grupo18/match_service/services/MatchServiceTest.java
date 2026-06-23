package com.grupo18.match_service.services;

import com.grupo18.match_service.clients.RegistrationClient;
import com.grupo18.match_service.clients.TournamentClient;
import com.grupo18.match_service.exceptions.MatchException;
import com.grupo18.match_service.models.Match;
import com.grupo18.match_service.models.dtos.TournamentDTO;
import com.grupo18.match_service.repositories.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock private MatchRepository matchRepository;
    @Mock private TournamentClient tournamentClient;
    @Mock private RegistrationClient registrationClient;

    @InjectMocks private MatchServiceImpl matchService;

    private Match matchPrueba;
    private TournamentDTO tournamentPrueba;

    @BeforeEach
    public void setUp() {
        matchPrueba = new Match();
        matchPrueba.setMatchId(1L);
        matchPrueba.setTournamentId(10L);
        matchPrueba.setHomeTeamId(100L);
        matchPrueba.setAwayTeamId(200L);
        matchPrueba.setMatchDate(LocalDateTime.now().plusDays(1));
        matchPrueba.setStatus("SCHEDULED");

        tournamentPrueba = new TournamentDTO();
        tournamentPrueba.setTournamentId(10L);
        tournamentPrueba.setState("UPCOMING");
    }

    @Test
    @DisplayName("Debe crear un partido válido entre dos equipos inscritos")
    public void shouldSaveMatch() {
        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);
        when(registrationClient.isTeamRegisteredInTournament(100L, 10L)).thenReturn(true);
        when(registrationClient.isTeamRegisteredInTournament(200L, 10L)).thenReturn(true);
        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        Match result = matchService.save(matchPrueba);

        assertThat(result.getStatus()).isEqualTo("SCHEDULED");
        verify(matchRepository, times(1)).save(matchPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el torneo no admite nuevos partidos")
    public void shouldThrowWhenTournamentNotAvailable() {
        tournamentPrueba.setState("FINISHED");
        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);

        assertThatThrownBy(() -> matchService.save(matchPrueba))
                .isInstanceOf(MatchException.class)
                .hasMessage("El torneo no existe o no permite nuevos partidos");
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el equipo local no está inscrito")
    public void shouldThrowWhenHomeTeamNotRegistered() {
        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);
        when(registrationClient.isTeamRegisteredInTournament(100L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> matchService.save(matchPrueba))
                .isInstanceOf(MatchException.class)
                .hasMessage("El equipo local no está inscrito en este torneo");
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si un equipo juega contra sí mismo")
    public void shouldThrowWhenSameTeam() {

        matchPrueba.setHomeTeamId(100L);
        matchPrueba.setAwayTeamId(100L);

        assertThatThrownBy(() -> matchService.save(matchPrueba))
                .isInstanceOf(MatchException.class)
                .hasMessage("Un equipo no puede jugar contra sí mismo");

        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @DisplayName("Debe buscar partido por ID")
    public void shouldFindMatchById() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(matchPrueba));

        Match result = matchService.findById(1L);

        assertThat(result).isNotNull();
        verify(matchRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar partido inexistente")
    public void shouldThrowWhenMatchNotFound() {
        when(matchRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.findById(9999L))
                .isInstanceOf(MatchException.class)
                .hasMessage("Partido no encontrado");
    }

    @Test
    @DisplayName("Debe cancelar (soft-delete) un partido")
    public void shouldCancelMatch() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(matchPrueba));
        when(matchRepository.save(any(Match.class))).thenReturn(matchPrueba);

        matchService.deleteById(1L);

        assertThat(matchPrueba.getStatus()).isEqualTo("CANCELLED");
        verify(matchRepository, times(1)).save(matchPrueba);
    }
}
