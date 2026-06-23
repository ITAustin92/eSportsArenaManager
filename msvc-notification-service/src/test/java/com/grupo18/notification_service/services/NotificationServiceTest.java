package com.grupo18.notification_service.services;

import com.grupo18.notification_service.clients.UserClient;
import com.grupo18.notification_service.models.Notification;
import com.grupo18.notification_service.models.dtos.NotificationRequestDTO;
import com.grupo18.notification_service.models.dtos.UserDTO;
import com.grupo18.notification_service.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserClient userClient;

    @InjectMocks private NotificationServiceImpl notificationService;

    private NotificationRequestDTO requestPrueba;

    private Notification notificationPrueba;



    @BeforeEach
    public void setUp() {
        requestPrueba = new NotificationRequestDTO(
                1L, null, 10L, "REGISTRATION_ACCEPTED", "Inscripción aceptada", "Tu equipo fue inscrito al torneo"
        );

        notificationPrueba = new Notification();
        notificationPrueba.setNotificationId(1L);
        notificationPrueba.setUserId(1L);
        notificationPrueba.setTeamId(5L);
        notificationPrueba.setTournamentId(10L);
    }

    @Test
    @DisplayName("Debe procesar y enviar una notificación correctamente")
    public void shouldProcessNotification() {
        UserDTO user = new UserDTO(1L, "Carlos", "Pérez", "carlos@esports.cl");

        when(userClient.getUserById(1L)).thenReturn(user);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification result = notificationService.processNotification(requestPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("SENT");
        assertThat(result.getSubject()).isEqualTo("Inscripción aceptada");
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe usar tipo SYSTEM_ALERT por defecto si no se especifica")
    public void shouldUseDefaultType() {
        NotificationRequestDTO sinTipo = new NotificationRequestDTO(1L, null, 10L, null, "Asunto", "Mensaje");
        UserDTO user = new UserDTO(1L, "Carlos", "Pérez", "carlos@esports.cl");

        when(userClient.getUserById(1L)).thenReturn(user);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        when(notificationRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        notificationService.processNotification(sinTipo);

        assertThat(captor.getValue().getType()).isEqualTo("SYSTEM_ALERT");
    }

    @Test
    @DisplayName("Debe marcar la notificación como FAILED si falla el envío")
    public void shouldMarkFailedOnError() {
        when(userClient.getUserById(1L)).thenThrow(new RuntimeException("Servicio no disponible"));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification result = notificationService.processNotification(requestPrueba);

        assertThat(result.getStatus()).isEqualTo("FAILED");
    }

    @Test
    @DisplayName("Debe buscar notificación por ID")
    public void shouldFindNotificationById() {
        Notification n = new Notification();
        n.setNotificationId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        Notification result = notificationService.findById(1L);

        assertThat(result).isNotNull();
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar notificación inexistente")
    public void shouldThrowWhenNotificationNotFound() {
        when(notificationRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.findById(9999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Notificación no encontrada");
    }

    @Test
    @DisplayName("Debe listar notificaciones por usuario")
    public void shouldFindByUserId() {
        Notification n = new Notification();
        n.setUserId(1L);
        when(notificationRepository.findByUserId(1L)).thenReturn(List.of(n));

        List<Notification> result = notificationService.findByUserId(1L);

        assertThat(result).hasSize(1);
        verify(notificationRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Debe listar todas las notificaciones")
    public void shouldListAllNotifications() {
        // Given
        when(notificationRepository.findAll()).thenReturn(List.of(notificationPrueba));

        // When
        List<Notification> result = notificationService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar notificaciones por equipo")
    public void shouldFindByTeamId() {
        // Given
        when(notificationRepository.findByTeamId(5L)).thenReturn(List.of(notificationPrueba));

        // When
        List<Notification> result = notificationService.findByTeamId(5L);

        // Then
        assertThat(result).hasSize(1);
        verify(notificationRepository, times(1)).findByTeamId(5L);
    }

    @Test
    @DisplayName("Debe buscar notificaciones por torneo")
    public void shouldFindByTournamentId() {
        // Given
        when(notificationRepository.findByTournamentId(10L)).thenReturn(List.of(notificationPrueba));

        // When
        List<Notification> result = notificationService.findByTournamentId(10L);

        // Then
        assertThat(result).hasSize(1);
        verify(notificationRepository, times(1)).findByTournamentId(10L);
    }

}