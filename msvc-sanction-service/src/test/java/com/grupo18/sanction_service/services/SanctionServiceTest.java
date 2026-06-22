package com.grupo18.sanction_service.services;

import com.grupo18.sanction_service.clients.TeamClient;
import com.grupo18.sanction_service.clients.TournamentClient;
import com.grupo18.sanction_service.clients.UserClient;
import com.grupo18.sanction_service.models.Sanction;
import com.grupo18.sanction_service.models.dtos.TeamDTO;
import com.grupo18.sanction_service.models.dtos.TournamentDTO;
import com.grupo18.sanction_service.models.dtos.UserDTO;
import com.grupo18.sanction_service.repositories.SanctionRepository;
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
public class SanctionServiceTest {

    @Mock private SanctionRepository sanctionRepository;
    @Mock private TournamentClient tournamentClient;
    @Mock private TeamClient teamClient;
    @Mock private UserClient userClient;

    @InjectMocks private SanctionServiceImpl sanctionService;

    private Sanction sanctionPrueba;

    @BeforeEach
    public void setUp() {
        sanctionPrueba = new Sanction();
        sanctionPrueba.setSanctionId(1L);
        sanctionPrueba.setTournamentId(1L);
        sanctionPrueba.setTeamId(1L);
        sanctionPrueba.setUserId(5L);
        sanctionPrueba.setType("SUSPENSION");
        sanctionPrueba.setReason("Conducta antideportiva");
        sanctionPrueba.setMatchesSuspended(2);
        sanctionPrueba.setFineAmount(0.0);
        sanctionPrueba.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("Debe guardar una sanción válida")
    public void shouldSaveSanction() {
        TournamentDTO tournament = new TournamentDTO();
        TeamDTO team = new TeamDTO();
        UserDTO user = new UserDTO();

        when(tournamentClient.getTournamentById(1L)).thenReturn(tournament);
        when(teamClient.getTeamById(1L)).thenReturn(team);
        when(userClient.getUserById(5L)).thenReturn(user);
        when(sanctionRepository.save(sanctionPrueba)).thenReturn(sanctionPrueba);

        Sanction result = sanctionService.save(sanctionPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("SUSPENSION");
        verify(sanctionRepository, times(1)).save(sanctionPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el torneo no existe")
    public void shouldThrowWhenTournamentNotFound() {
        when(tournamentClient.getTournamentById(1L)).thenReturn(null);

        assertThatThrownBy(() -> sanctionService.save(sanctionPrueba))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El torneo indicado no existe");
        verify(sanctionRepository, never()).save(any(Sanction.class));
    }

    @Test
    @DisplayName("Debe buscar sanción por ID")
    public void shouldFindSanctionById() {
        when(sanctionRepository.findById(1L)).thenReturn(Optional.of(sanctionPrueba));

        Sanction result = sanctionService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getReason()).isEqualTo("Conducta antideportiva");
        verify(sanctionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar sanción inexistente")
    public void shouldThrowWhenSanctionNotFound() {
        when(sanctionRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sanctionService.findById(9999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Sanción no encontrada");
    }

    @Test
    @DisplayName("Debe actualizar el estado de una sanción")
    public void shouldUpdateStatus() {
        when(sanctionRepository.findById(1L)).thenReturn(Optional.of(sanctionPrueba));
        when(sanctionRepository.save(any(Sanction.class))).thenReturn(sanctionPrueba);

        Sanction result = sanctionService.updateStatus(1L, "CLOSED");

        assertThat(result.getStatus()).isEqualTo("CLOSED");
        verify(sanctionRepository, times(1)).save(sanctionPrueba);
    }

    @Test
    @DisplayName("Debe eliminar una sanción")
    public void shouldDeleteSanction() {
        when(sanctionRepository.findById(1L)).thenReturn(Optional.of(sanctionPrueba));
        doNothing().when(sanctionRepository).delete(sanctionPrueba);

        sanctionService.deleteById(1L);

        verify(sanctionRepository, times(1)).delete(sanctionPrueba);
    }

    @Test
    @DisplayName("Debe listar sanciones por equipo")
    public void shouldFindByTeamId() {
        when(sanctionRepository.findByTeamId(1L)).thenReturn(List.of(sanctionPrueba));

        List<Sanction> result = sanctionService.findByTeamId(1L);

        assertThat(result).hasSize(1);
        verify(sanctionRepository, times(1)).findByTeamId(1L);
    }
}
