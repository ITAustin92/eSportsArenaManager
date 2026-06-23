package com.grupo18.notification_service.services;

import com.grupo18.notification_service.clients.UserClient;
import com.grupo18.notification_service.models.Notification;
import com.grupo18.notification_service.models.dtos.NotificationRequestDTO;
import com.grupo18.notification_service.models.dtos.UserDTO;
import com.grupo18.notification_service.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {



    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserClient userClient;

    @Transactional
    @Override
    public Notification processNotification(NotificationRequestDTO request) {
        log.info("Procesando nueva notificación. Tipo: '{}', Asunto: '{}'",
                request.getType(), request.getSubject());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTeamId(request.getTeamId());
        notification.setTournamentId(request.getTournamentId());
        notification.setType(request.getType() != null ? request.getType() : "SYSTEM_ALERT");
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setStatus("PENDING");

        notification = notificationRepository.save(notification);
        log.info("Notificación ID {} guardada en BD con estado PENDING", notification.getNotificationId());

        try {
            String emailDestino = "alertas_equipo@torneo.com";
            if (notification.getUserId() != null) {
                log.info("Consultando correo del usuario ID {} al user-service", notification.getUserId());
                UserDTO user = userClient.getUserById(notification.getUserId());
                if (user != null && user.getEmail() != null) {
                    emailDestino = user.getEmail();
                    log.info("Correo obtenido: '{}'", emailDestino);
                }
            }

            log.info("=== SIMULACIÓN DE ENVÍO ===");
            log.info("[NOTIFICATION-SERVICE] Destino: {}", emailDestino);
            log.info("[NOTIFICATION-SERVICE] Asunto: {}", notification.getSubject());
            log.info("[NOTIFICATION-SERVICE] Mensaje: {}", notification.getMessage());
            log.info("===========================");

            notification.setStatus("SENT");
            log.info("Notificación ID {} enviada exitosamente", notification.getNotificationId());

        } catch (Exception e) {
            log.error("Fallo en el envío de notificación ID {}. Causa: {}",
                    notification.getNotificationId(), e.getMessage());
            notification.setStatus("FAILED");
        }

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public Notification findById(Long id) {
        log.info("Buscando notificación con ID: {}", id);
        return notificationRepository.findById(id).orElseThrow(() -> {
            log.warn("Notificación con ID {} no encontrada", id);
            return new RuntimeException("Notificación no encontrada");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findAll() {
        log.info("Obteniendo listado completo de notificaciones");
        List<Notification> lista = notificationRepository.findAll();
        log.info("Se encontraron {} notificación(es) en total", lista.size());
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findByUserId(Long userId) {
        log.info("Buscando notificaciones del usuario ID: {}", userId);
        List<Notification> lista = notificationRepository.findByUserId(userId);
        log.info("Se encontraron {} notificación(es) para usuario ID: {}", lista.size(), userId);
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findByTeamId(Long teamId) {
        log.info("Buscando notificaciones del equipo ID: {}", teamId);
        List<Notification> lista = notificationRepository.findByTeamId(teamId);
        log.info("Se encontraron {} notificación(es) para equipo ID: {}", lista.size(), teamId);
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findByTournamentId(Long tournamentId) {
        log.info("Buscando notificaciones del torneo ID: {}", tournamentId);
        List<Notification> lista = notificationRepository.findByTournamentId(tournamentId);
        log.info("Se encontraron {} notificación(es) para torneo ID: {}", lista.size(), tournamentId);
        return lista;
    }
}