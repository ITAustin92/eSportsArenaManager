package com.grupo18.team_service.services;

import com.grupo18.team_service.exceptions.TeamException;
import com.grupo18.team_service.models.MemberTeam;
import com.grupo18.team_service.models.Team;
import com.grupo18.team_service.repositories.TeamRepository;
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
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team teamPrueba;

    @BeforeEach
    public void setUp() {
        teamPrueba = new Team();
        teamPrueba.setEquipoId(1L);
        teamPrueba.setNombre("Los Invencibles");
        teamPrueba.setCapitanId(10L);
        teamPrueba.setJuegoPrincipalId(1L);
        teamPrueba.setEstado("ACTIVO");
        teamPrueba.setMiembros(new ArrayList<>());
    }

    @Test
    @DisplayName("Debe listar todos los equipos")
    public void shouldListAllTeams() {
        when(teamRepository.findAll()).thenReturn(List.of(teamPrueba));

        List<Team> result = teamService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result).contains(teamPrueba);
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar equipo por ID")
    public void shouldFindTeamById() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamPrueba));

        Team result = teamService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Los Invencibles");
        verify(teamRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar equipo inexistente")
    public void shouldThrowWhenTeamNotFound() {
        when(teamRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.findById(9999L))
                .isInstanceOf(TeamException.class)
                .hasMessage("Equipo no encontrado");
        verify(teamRepository, times(1)).findById(9999L);
    }

    @Test
    @DisplayName("Debe guardar un equipo nuevo")
    public void shouldSaveTeam() {
        when(teamRepository.findByNombre("Los Invencibles")).thenReturn(Optional.empty());
        when(teamRepository.save(teamPrueba)).thenReturn(teamPrueba);

        Team result = teamService.save(teamPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Los Invencibles");
        verify(teamRepository, times(1)).save(teamPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar equipo con nombre duplicado")
    public void shouldThrowWhenTeamNameExists() {
        when(teamRepository.findByNombre("Los Invencibles")).thenReturn(Optional.of(teamPrueba));

        assertThatThrownBy(() -> teamService.save(teamPrueba))
                .isInstanceOf(TeamException.class)
                .hasMessage("El nombre del equipo ya está en uso");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al duplicar jugador en el mismo equipo")
    public void shouldThrowWhenDuplicatedPlayer() {
        MemberTeam m1 = new MemberTeam(); m1.setUsuarioId(5L);
        MemberTeam m2 = new MemberTeam(); m2.setUsuarioId(5L);
        teamPrueba.setMiembros(new ArrayList<>(List.of(m1, m2)));

        when(teamRepository.findByNombre("Los Invencibles")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.save(teamPrueba))
                .isInstanceOf(TeamException.class)
                .hasMessageContaining("No se puede duplicar un jugador");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Debe actualizar un equipo existente")
    public void shouldUpdateTeam() {
        Team cambios = new Team();
        cambios.setNombre("Los Invencibles");
        cambios.setCapitanId(11L);
        cambios.setEstado("ACTIVO");
        cambios.setMiembros(new ArrayList<>());

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamPrueba));
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        Team result = teamService.updateById(1L, cambios);

        assertThat(result.getCapitanId()).isEqualTo(11L);
        verify(teamRepository, times(1)).save(teamPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar equipo inexistente")
    public void shouldThrowWhenUpdateNotFound() {
        when(teamRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.updateById(9999L, teamPrueba))
                .isInstanceOf(TeamException.class)
                .hasMessage("Equipo no encontrado");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Debe desactivar un equipo")
    public void shouldDeactivateTeam() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamPrueba));
        when(teamRepository.save(any(Team.class))).thenReturn(teamPrueba);

        teamService.deleteById(1L);

        assertThat(teamPrueba.getEstado()).isEqualTo("INACTIVO");
        verify(teamRepository, times(1)).save(teamPrueba);
    }

    @Test
    @DisplayName("Debe filtrar equipos por estado")
    public void shouldFindByEstado() {
        when(teamRepository.findByEstado("ACTIVO")).thenReturn(List.of(teamPrueba));

        List<Team> result = teamService.findByEstado("ACTIVO");

        assertThat(result).hasSize(1);
        verify(teamRepository, times(1)).findByEstado("ACTIVO");
    }

    @Test
    @DisplayName("Debe buscar equipos por juego principal")
    public void shouldFindByJuegoPrincipalId() {
        // Given
        when(teamRepository.findByJuegoPrincipalId(1L)).thenReturn(List.of(teamPrueba));

        // When
        List<Team> result = teamService.findByJuegoPrincipalId(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(teamRepository, times(1)).findByJuegoPrincipalId(1L);
    }

    @Test
    @DisplayName("Debe buscar equipos por capitán")
    public void shouldFindByCapitanId() {
        // Given
        when(teamRepository.findByCapitanId(1L)).thenReturn(List.of(teamPrueba));

        // When
        List<Team> result = teamService.findByCapitanId(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(teamRepository, times(1)).findByCapitanId(1L);
    }

}