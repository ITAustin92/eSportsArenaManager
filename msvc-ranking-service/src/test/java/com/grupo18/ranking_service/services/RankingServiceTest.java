package com.grupo18.ranking_service.services;

import com.grupo18.ranking_service.clients.TournamentClient;
import com.grupo18.ranking_service.models.Ranking;
import com.grupo18.ranking_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.ranking_service.models.dtos.TournamentDTO;
import com.grupo18.ranking_service.repositories.RankingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RankingServiceTest {

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private TournamentClient tournamentClient;

    @InjectMocks
    private RankingServiceImpl rankingService;

    @Test
    @DisplayName("Debe sumar +3 puntos al ganador y +1 derrota al perdedor")
    public void shouldUpdateRankingOnMatchResult() {
        TournamentDTO t = new TournamentDTO();
        t.setState("UPCOMING");

        Ranking winner = new Ranking();
        winner.setPoints(0);
        winner.setWins(0);

        Ranking loser = new Ranking();
        loser.setPoints(0);
        loser.setLosses(0);

        when(tournamentClient.getTournamentById(1L)).thenReturn(t);
        when(rankingRepository.findByTournamentIdAndTeamId(1L, 100L)).thenReturn(Optional.of(winner));
        when(rankingRepository.findByTournamentIdAndTeamId(1L, 200L)).thenReturn(Optional.of(loser));
        when(rankingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));


        MatchResultUpdateDTO dto = new MatchResultUpdateDTO();
        dto.setTournamentId(1L);
        dto.setWinnerTeamId(100L);
        dto.setLoserTeamId(200L);

        rankingService.processMatchResult(dto);

        assertThat(winner.getPoints()).isEqualTo(3);
        assertThat(winner.getWins()).isEqualTo(1);
        assertThat(loser.getLosses()).isEqualTo(1);
        verify(rankingRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Debe crear entrada nueva si el equipo aún no tiene registro en el ranking")
    public void shouldCreateRankingEntryIfNotExists() {
        TournamentDTO t = new TournamentDTO();
        t.setState("UPCOMING");

        when(tournamentClient.getTournamentById(1L)).thenReturn(t);
        when(rankingRepository.findByTournamentIdAndTeamId(1L, 100L)).thenReturn(Optional.empty());
        when(rankingRepository.findByTournamentIdAndTeamId(1L, 200L)).thenReturn(Optional.empty());
        when(rankingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));


        MatchResultUpdateDTO dto = new MatchResultUpdateDTO();
        dto.setTournamentId(1L);
        dto.setWinnerTeamId(100L);
        dto.setLoserTeamId(200L);

        rankingService.processMatchResult(dto);

        verify(rankingRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Debe retornar la tabla de posiciones de un torneo")
    public void shouldGetTournamentLeaderboard() {
        // Given
        Ranking r1 = new Ranking();
        r1.setTournamentId(1L);
        r1.setTeamId(10L);
        r1.setPoints(9);

        Ranking r2 = new Ranking();
        r2.setTournamentId(1L);
        r2.setTeamId(20L);
        r2.setPoints(6);

        when(rankingRepository.findByTournamentIdOrderByPointsDesc(1L))
                .thenReturn(List.of(r1, r2));

        // When
        List<Ranking> result = rankingService.getTournamentLeaderboard(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPoints()).isEqualTo(9);
        verify(rankingRepository, times(1)).findByTournamentIdOrderByPointsDesc(1L);
    }

    @Test
    @DisplayName("Debe encontrar el ranking de un equipo en un torneo")
    public void shouldFindByTournamentAndTeam() {
        // Given
        Ranking ranking = new Ranking();
        ranking.setTournamentId(1L);
        ranking.setTeamId(10L);
        ranking.setPoints(6);

        when(rankingRepository.findByTournamentIdAndTeamId(1L, 10L))
                .thenReturn(Optional.of(ranking));

        // When
        Optional<Ranking> result = rankingService.findByTournamentAndTeam(1L, 10L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPoints()).isEqualTo(6);
        verify(rankingRepository, times(1)).findByTournamentIdAndTeamId(1L, 10L);
    }

    @Test
    @DisplayName("Debe retornar vacío si el equipo no tiene ranking en el torneo")
    public void shouldReturnEmptyWhenRankingNotFound() {
        // Given
        when(rankingRepository.findByTournamentIdAndTeamId(1L, 99L))
                .thenReturn(Optional.empty());

        // When
        Optional<Ranking> result = rankingService.findByTournamentAndTeam(1L, 99L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe lanzar excepción si el torneo está finalizado al procesar resultado")
    public void shouldThrowWhenTournamentFinished() {
        // Given
        TournamentDTO torneoFinalizado = new TournamentDTO();
        torneoFinalizado.setState("FINISHED");

        when(tournamentClient.getTournamentById(1L)).thenReturn(torneoFinalizado);

        MatchResultUpdateDTO dto = new MatchResultUpdateDTO();
        dto.setTournamentId(1L);
        dto.setWinnerTeamId(10L);
        dto.setLoserTeamId(20L);

        // Then
        assertThatThrownBy(() -> rankingService.processMatchResult(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pueden sumar puntos");
    }

}