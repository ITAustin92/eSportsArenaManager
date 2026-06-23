package com.grupo18.tournament_service.services;

import com.grupo18.tournament_service.clients.GameClient;
import com.grupo18.tournament_service.clients.UserClient;
import com.grupo18.tournament_service.exceptions.TournamentException;
import com.grupo18.tournament_service.models.Tournament;
import com.grupo18.tournament_service.models.dtos.GameDTO;
import com.grupo18.tournament_service.models.dtos.UserDTO;
import com.grupo18.tournament_service.repositories.TournamentRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock private TournamentRepository tournamentRepository;
    @Mock private GameClient gameClient;
    @Mock private UserClient userClient;

    @InjectMocks private TournamentServiceImpl tournamentService;

    private Tournament tournamentPrueba;
    private List<Tournament> tournamentList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        tournamentPrueba = new Tournament();
        tournamentPrueba.setTournamentId(1L);
        tournamentPrueba.setName("Copa eSports 2026");
        tournamentPrueba.setGameId(1L);
        tournamentPrueba.setOrganizerId(2L);
        tournamentPrueba.setStartDate(LocalDate.now().plusDays(10));
        tournamentPrueba.setEndDate(LocalDate.now().plusDays(20));
        tournamentPrueba.setState("UPCOMING");

        Faker faker = new Faker();
        for (int i = 0; i < 5; i++) {
            Tournament t = new Tournament();
            t.setName(faker.esports().team() + " Cup " + i);
            t.setGameId(1L);
            t.setOrganizerId(1L);
            t.setStartDate(LocalDate.now().plusDays(5));
            t.setEndDate(LocalDate.now().plusDays(10));
            t.setState("UPCOMING");
            tournamentList.add(t);
        }
    }

    @Test
    @DisplayName("Debe listar todos los torneos")
    public void shouldListAllTournaments() {
        List<Tournament> all = new ArrayList<>(tournamentList);
        all.add(tournamentPrueba);
        when(tournamentRepository.findAll()).thenReturn(all);

        List<Tournament> result = tournamentService.findAll();

        assertThat(result).hasSize(6);
        assertThat(result).contains(tournamentPrueba);
        verify(tournamentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar torneo por ID")
    public void shouldFindTournamentById() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentPrueba));

        Tournament result = tournamentService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Copa eSports 2026");
        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar torneo inexistente")
    public void shouldThrowWhenTournamentNotFound() {
        when(tournamentRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.findById(9999L))
                .isInstanceOf(TournamentException.class)
                .hasMessage("Torneo no encontrado");
        verify(tournamentRepository, times(1)).findById(9999L);
    }

    @Test
    @DisplayName("Debe listar torneos por estado")
    public void shouldFindByState() {
        when(tournamentRepository.findByState("UPCOMING")).thenReturn(tournamentList);

        List<Tournament> result = tournamentService.findByState("UPCOMING");

        assertThat(result).hasSize(5);
        verify(tournamentRepository, times(1)).findByState("UPCOMING");
    }

    @Test
    @DisplayName("Debe guardar un torneo válido")
    public void shouldSaveTournament() {
        GameDTO game = new GameDTO();
        game.setEstado("ACTIVO");
        UserDTO organizer = new UserDTO();
        organizer.setState("ACTIVO");

        when(tournamentRepository.findByName(tournamentPrueba.getName())).thenReturn(Optional.empty());
        when(gameClient.getGameById(1L)).thenReturn(game);
        when(userClient.getUserById(2L)).thenReturn(organizer);
        when(tournamentRepository.save(tournamentPrueba)).thenReturn(tournamentPrueba);

        Tournament result = tournamentService.save(tournamentPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Copa eSports 2026");
        verify(tournamentRepository, times(1)).save(tournamentPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la fecha de termino es anterior a la de inicio")
    public void shouldThrowWhenEndDateBeforeStartDate() {
        tournamentPrueba.setEndDate(tournamentPrueba.getStartDate().minusDays(5));
        when(tournamentRepository.findByName(tournamentPrueba.getName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.save(tournamentPrueba))
                .isInstanceOf(TournamentException.class)
                .hasMessage("La fecha de termino no puede ser anterior a la de inicio");
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar torneo inexistente")
    public void shouldThrowWhenUpdateNotFound() {
        when(tournamentRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.updateById(9999L, tournamentPrueba))
                .isInstanceOf(TournamentException.class)
                .hasMessage("Torneo no encontrado para actualizar");
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Debe cancelar (soft-delete) un torneo")
    public void shouldCancelTournament() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentPrueba));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournamentPrueba);

        tournamentService.deleteById(1L);

        assertThat(tournamentPrueba.getState()).isEqualTo("CANCELLED");
        verify(tournamentRepository, times(1)).save(tournamentPrueba);
    }

    @Test
    @DisplayName("Debe buscar torneos por juego")
    public void shouldFindByGameId() {
        // Given
        when(tournamentRepository.findByGameId(1L)).thenReturn(List.of(tournamentPrueba));

        // When
        List<Tournament> result = tournamentService.findByGameId(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(tournamentRepository, times(1)).findByGameId(1L);
    }

    @Test
    @DisplayName("Debe buscar torneos por organizador")
    public void shouldFindByOrganizerId() {
        // Given
        when(tournamentRepository.findByOrganizerId(1L)).thenReturn(List.of(tournamentPrueba));

        // When
        List<Tournament> result = tournamentService.findByOrganizerId(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(tournamentRepository, times(1)).findByOrganizerId(1L);
    }

    @Test
    @DisplayName("Debe actualizar un torneo existente")
    public void shouldUpdateTournament() {
        // Given
        Tournament cambios = new Tournament();
        cambios.setName(tournamentPrueba.getName()); // mismo nombre, sin conflicto
        cambios.setStartDate(tournamentPrueba.getStartDate());
        cambios.setEndDate(tournamentPrueba.getEndDate());
        cambios.setState("IN_PROGRESS");

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentPrueba));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Tournament result = tournamentService.updateById(1L, cambios);

        // Then
        assertThat(result.getState()).isEqualTo("IN_PROGRESS");
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar torneo inexistente")
    public void shouldThrowWhenUpdateTournamentNotFound() {
        // Given
        when(tournamentRepository.findById(9999L)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> tournamentService.updateById(9999L, tournamentPrueba))
                .isInstanceOf(TournamentException.class)
                .hasMessage("Torneo no encontrado para actualizar");
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

}