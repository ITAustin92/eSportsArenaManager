package com.grupo18.prize_service.services;

import com.grupo18.prize_service.clients.RankingClient;
import com.grupo18.prize_service.clients.TournamentClient;
import com.grupo18.prize_service.models.Prize;
import com.grupo18.prize_service.models.dtos.RankingDTO;
import com.grupo18.prize_service.models.dtos.TournamentDTO;
import com.grupo18.prize_service.repositories.PrizeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrizeServiceTest {

    @Mock private PrizeRepository prizeRepository;
    @Mock private TournamentClient tournamentClient;
    @Mock private RankingClient rankingClient;

    @InjectMocks private PrizeServiceImpl prizeService;

    private Prize prizePrueba;
    private TournamentDTO tournamentPrueba;

    @BeforeEach
    public void setUp() {
        prizePrueba = new Prize();
        prizePrueba.setPrizeId(1L);
        prizePrueba.setTournamentId(10L);
        prizePrueba.setDescription("Trofeo + premio en efectivo");
        prizePrueba.setType("CASH");
        prizePrueba.setAmount(500000.0);
        prizePrueba.setStatus("PENDING");

        tournamentPrueba = new TournamentDTO();
        tournamentPrueba.setTournamentId(10L);
        tournamentPrueba.setState("UPCOMING");
    }

    @Test
    @DisplayName("Debe guardar un premio si el torneo existe")
    public void shouldSavePrize() {
        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);
        when(prizeRepository.save(prizePrueba)).thenReturn(prizePrueba);

        Prize result = prizeService.save(prizePrueba);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("CASH");
        verify(prizeRepository, times(1)).save(prizePrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el torneo no existe")
    public void shouldThrowWhenTournamentNotFound() {
        when(tournamentClient.getTournamentById(10L)).thenReturn(null);

        assertThatThrownBy(() -> prizeService.save(prizePrueba))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El torneo indicado no existe");
        verify(prizeRepository, never()).save(any(Prize.class));
    }

    @Test
    @DisplayName("Debe buscar premio por ID")
    public void shouldFindPrizeById() {
        when(prizeRepository.findById(1L)).thenReturn(Optional.of(prizePrueba));

        Prize result = prizeService.findById(1L);

        assertThat(result).isNotNull();
        verify(prizeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar premio inexistente")
    public void shouldThrowWhenPrizeNotFound() {
        when(prizeRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prizeService.findById(9999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Premio no encontrado");
    }

    @Test
    @DisplayName("Debe distribuir premios según el ranking cuando el torneo está finalizado")
    public void shouldDistributePrizes() {
        tournamentPrueba.setState("FINISHED");
        RankingDTO primerLugar = new RankingDTO(1L, 10L, 500L, 9, 3, 0, 3);

        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);
        when(prizeRepository.findByTournamentIdAndStatus(10L, "PENDING")).thenReturn(new ArrayList<>(List.of(prizePrueba)));
        when(rankingClient.getTournamentLeaderboard(10L)).thenReturn(List.of(primerLugar));
        when(prizeRepository.save(any(Prize.class))).thenReturn(prizePrueba);

        prizeService.distributePrizesForTournament(10L);

        assertThat(prizePrueba.getStatus()).isEqualTo("DELIVERED");
        assertThat(prizePrueba.getTeamId()).isEqualTo(500L);
        verify(prizeRepository, times(1)).save(prizePrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al distribuir premios de un torneo no finalizado")
    public void shouldThrowWhenTournamentNotFinished() {
        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);

        assertThatThrownBy(() -> prizeService.distributePrizesForTournament(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Solo se pueden repartir premios cuando el torneo está en estado FINISHED");
        verify(prizeRepository, never()).save(any(Prize.class));
    }

    @Test
    @DisplayName("Debe listar todos los premios")
    public void shouldListAllPrizes() {
        // Given
        when(prizeRepository.findAll()).thenReturn(List.of(prizePrueba));

        // When
        List<Prize> result = prizeService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(prizeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar premios por torneo")
    public void shouldFindByTournamentId() {
        // Given
        when(prizeRepository.findByTournamentId(1L)).thenReturn(List.of(prizePrueba));

        // When
        List<Prize> result = prizeService.findByTournamentId(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(prizeRepository, times(1)).findByTournamentId(1L);
    }

    @Test
    @DisplayName("Debe buscar premios por equipo ganador")
    public void shouldFindByTeamId() {
        // Given
        when(prizeRepository.findByTeamId(10L)).thenReturn(List.of(prizePrueba));

        // When
        List<Prize> result = prizeService.findByTeamId(10L);

        // Then
        assertThat(result).hasSize(1);
        verify(prizeRepository, times(1)).findByTeamId(10L);
    }

}