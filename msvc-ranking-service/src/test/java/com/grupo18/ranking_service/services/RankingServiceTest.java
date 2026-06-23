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
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

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
}