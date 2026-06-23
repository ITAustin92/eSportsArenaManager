package com.grupo18.result_service.services;

import com.grupo18.result_service.clients.MatchClient;
import com.grupo18.result_service.clients.RankingClient;
import com.grupo18.result_service.exceptions.ResultException;
import com.grupo18.result_service.models.Result;
import com.grupo18.result_service.models.dtos.MatchDTO;
import com.grupo18.result_service.repositories.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResultServiceTest {

    @Mock private ResultRepository resultRepository;
    @Mock private MatchClient matchClient;
    @Mock private RankingClient rankingClient;

    @InjectMocks private ResultServiceImpl resultService;

    private Result resultPrueba;
    private MatchDTO matchPrueba;

    @BeforeEach
    public void setUp() {
        resultPrueba = new Result();
        resultPrueba.setResultId(1L);
        resultPrueba.setMatchId(10L);
        resultPrueba.setWinnerTeamId(100L);
        resultPrueba.setHomeScore(3);
        resultPrueba.setAwayScore(1);
        resultPrueba.setStatus("PENDING");

        matchPrueba = new MatchDTO();
        matchPrueba.setMatchId(10L);
        matchPrueba.setTournamentId(1L);
        matchPrueba.setHomeTeamId(100L);
        matchPrueba.setAwayTeamId(200L);
        matchPrueba.setStatus("FINISHED");
    }

    @Test
    @DisplayName("Debe registrar un resultado válido y notificar al ranking")
    public void shouldSaveResult() {
        when(matchClient.getMatchById(10L)).thenReturn(matchPrueba);
        when(resultRepository.existsByMatchId(10L)).thenReturn(false);
        when(resultRepository.save(any(Result.class))).thenAnswer(inv -> inv.getArgument(0));

        Result result = resultService.save(resultPrueba);

        assertThat(result.getStatus()).isEqualTo("CONFIRMED");
        verify(rankingClient, times(1)).updateRanking(any());
        verify(resultRepository, times(1)).save(resultPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el partido no existe")
    public void shouldThrowWhenMatchNotFound() {
        when(matchClient.getMatchById(10L)).thenReturn(null);

        assertThatThrownBy(() -> resultService.save(resultPrueba))
                .isInstanceOf(ResultException.class)
                .hasMessage("El partido reportado no existe");
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un resultado para ese partido")
    public void shouldThrowWhenResultAlreadyExists() {
        when(matchClient.getMatchById(10L)).thenReturn(matchPrueba);
        when(resultRepository.existsByMatchId(10L)).thenReturn(true);

        assertThatThrownBy(() -> resultService.save(resultPrueba))
                .isInstanceOf(ResultException.class)
                .hasMessage("Ya existe un resultado registrado para este partido");
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el ganador no jugó el partido")
    public void shouldThrowWhenWinnerNotInMatch() {
        resultPrueba.setWinnerTeamId(999L);
        when(matchClient.getMatchById(10L)).thenReturn(matchPrueba);
        when(resultRepository.existsByMatchId(10L)).thenReturn(false);

        assertThatThrownBy(() -> resultService.save(resultPrueba))
                .isInstanceOf(ResultException.class)
                .hasMessage("El ganador debe ser uno de los equipos que jugó el partido");
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    @DisplayName("Debe buscar resultado por ID")
    public void shouldFindResultById() {
        when(resultRepository.findById(1L)).thenReturn(Optional.of(resultPrueba));

        Result result = resultService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getHomeScore()).isEqualTo(3);
        verify(resultRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar resultado inexistente")
    public void shouldThrowWhenResultNotFound() {
        when(resultRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultService.findById(9999L))
                .isInstanceOf(ResultException.class)
                .hasMessage("Resultado no encontrado");
    }

    @Test
    @DisplayName("Debe anular (soft-delete) un resultado")
    public void shouldAnnulResult() {
        when(resultRepository.findById(1L)).thenReturn(Optional.of(resultPrueba));
        when(resultRepository.save(any(Result.class))).thenReturn(resultPrueba);

        resultService.deleteById(1L);

        assertThat(resultPrueba.getStatus()).isEqualTo("ANULADO");
        verify(resultRepository, times(1)).save(resultPrueba);
    }

    @Test
    @DisplayName("Debe listar todos los resultados")
    public void shouldListAllResults() {
        // Given
        when(resultRepository.findAll()).thenReturn(List.of(resultPrueba));

        // When
        List<Result> result = resultService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(resultRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar resultado por ID de partido")
    public void shouldFindByMatchId() {
        // Given
        when(resultRepository.findByMatchId(5L)).thenReturn(Optional.of(resultPrueba));

        // When
        Result result = resultService.findByMatchId(5L);

        // Then
        assertThat(result).isNotNull();
        verify(resultRepository, times(1)).findByMatchId(5L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar resultado por partido inexistente")
    public void shouldThrowWhenFindByMatchIdNotFound() {
        // Given
        when(resultRepository.findByMatchId(9999L)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> resultService.findByMatchId(9999L))
                .isInstanceOf(ResultException.class)
                .hasMessage("Resultado no encontrado para el partido");
    }

    @Test
    @DisplayName("Debe buscar resultados por equipo ganador")
    public void shouldFindByWinnerTeamId() {
        // Given
        when(resultRepository.findByWinnerTeamId(10L)).thenReturn(List.of(resultPrueba));

        // When
        List<Result> results = resultService.findByWinnerTeamId(10L);

        // Then
        assertThat(results).hasSize(1);
        verify(resultRepository, times(1)).findByWinnerTeamId(10L);
    }

    @Test
    @DisplayName("Debe actualizar un resultado existente")
    public void shouldUpdateResult() {
        // Given
        Result cambios = new Result();
        cambios.setHomeScore(3);
        cambios.setAwayScore(1);
        cambios.setWinnerTeamId(10L);

        when(resultRepository.findById(1L)).thenReturn(Optional.of(resultPrueba));
        when(resultRepository.save(any(Result.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Result updated = resultService.updateById(1L, cambios);

        // Then
        assertThat(updated.getHomeScore()).isEqualTo(3);
        assertThat(updated.getAwayScore()).isEqualTo(1);
        assertThat(updated.getWinnerTeamId()).isEqualTo(10L);
        verify(resultRepository, times(1)).save(any(Result.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar resultado inexistente")
    public void shouldThrowWhenUpdateResultNotFound() {
        // Given
        when(resultRepository.findById(9999L)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> resultService.updateById(9999L, resultPrueba))
                .isInstanceOf(ResultException.class)
                .hasMessage("Resultado no encontrado");
        verify(resultRepository, never()).save(any(Result.class));
    }

}