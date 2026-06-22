package com.grupo18.registration_service.services;

import com.grupo18.registration_service.clients.TeamClient;
import com.grupo18.registration_service.clients.TournamentClient;
import com.grupo18.registration_service.exceptions.RegistrationException;
import com.grupo18.registration_service.models.Registration;
import com.grupo18.registration_service.models.dtos.TeamDTO;
import com.grupo18.registration_service.models.dtos.TournamentDTO;
import com.grupo18.registration_service.repositories.RegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock private RegistrationRepository registrationRepository;
    @Mock private TeamClient teamClient;
    @Mock private TournamentClient tournamentClient;

    @InjectMocks private RegistrationServiceImpl registrationService;

    private Registration registrationPrueba;
    private TeamDTO teamPrueba;
    private TournamentDTO tournamentPrueba;

    @BeforeEach
    public void setUp() {
        registrationPrueba = new Registration();
        registrationPrueba.setRegistrationId(1L);
        registrationPrueba.setTeamId(100L);
        registrationPrueba.setTournamentId(10L);

        teamPrueba = new TeamDTO();
        teamPrueba.setId(100L);
        teamPrueba.setEstado("ACTIVO");

        tournamentPrueba = new TournamentDTO();
        tournamentPrueba.setTournamentId(10L);
        tournamentPrueba.setState("UPCOMING");
    }

    @Test
    @DisplayName("Debe crear una inscripción válida")
    public void shouldSaveRegistration() {
        when(registrationRepository.findByTeamIdAndTournamentId(100L, 10L)).thenReturn(Optional.empty());
        when(teamClient.getTeamById(100L)).thenReturn(teamPrueba);
        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(inv -> inv.getArgument(0));

        Registration result = registrationService.save(registrationPrueba);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getRegistrationDate()).isNotNull();
        verify(registrationRepository, times(1)).save(registrationPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el equipo ya está inscrito en el torneo")
    public void shouldThrowWhenAlreadyRegistered() {
        when(registrationRepository.findByTeamIdAndTournamentId(100L, 10L)).thenReturn(Optional.of(registrationPrueba));

        assertThatThrownBy(() -> registrationService.save(registrationPrueba))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("El equipo ya se encuentra inscrito en este torneo");
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el equipo no está activo")
    public void shouldThrowWhenTeamNotActive() {
        teamPrueba.setEstado("INACTIVO");
        when(registrationRepository.findByTeamIdAndTournamentId(100L, 10L)).thenReturn(Optional.empty());
        when(teamClient.getTeamById(100L)).thenReturn(teamPrueba);

        assertThatThrownBy(() -> registrationService.save(registrationPrueba))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("El equipo no existe o no está activo");
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el torneo no admite inscripciones")
    public void shouldThrowWhenTournamentNotUpcoming() {
        tournamentPrueba.setState("FINISHED");
        when(registrationRepository.findByTeamIdAndTournamentId(100L, 10L)).thenReturn(Optional.empty());
        when(teamClient.getTeamById(100L)).thenReturn(teamPrueba);
        when(tournamentClient.getTournamentById(10L)).thenReturn(tournamentPrueba);

        assertThatThrownBy(() -> registrationService.save(registrationPrueba))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("El torneo no existe o no admite nuevas inscripciones");
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    @DisplayName("Debe actualizar el estado de una inscripción")
    public void shouldUpdateStatus() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registrationPrueba));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registrationPrueba);

        Registration result = registrationService.updateStatus(1L, "ACCEPTED");

        assertThat(result.getStatus()).isEqualTo("ACCEPTED");
        verify(registrationRepository, times(1)).save(registrationPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar inscripción inexistente")
    public void shouldThrowWhenUpdateNotFound() {
        when(registrationRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.updateStatus(9999L, "ACCEPTED"))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("Inscripción no encontrada");
    }

    @Test
    @DisplayName("Debe cancelar una inscripción")
    public void shouldCancelRegistration() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registrationPrueba));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registrationPrueba);

        registrationService.cancelById(1L);

        assertThat(registrationPrueba.getStatus()).isEqualTo("CANCELLED");
        verify(registrationRepository, times(1)).save(registrationPrueba);
    }
}
