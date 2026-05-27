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

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserClient userClient;

    @Transactional
    @Override
    public Notification processNotification(NotificationRequestDTO request) {

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTeamId(request.getTeamId());
        notification.setTournamentId(request.getTournamentId());
        notification.setType(request.getType() != null ? request.getType() : "SYSTEM_ALERT");
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setStatus("PENDING"); // Iniciamos asumiendo que está en cola

        notification = notificationRepository.save(notification);

        try {
            String emailDestino = "alertas_equipo@torneo.com";

            if (notification.getUserId() != null) {
                UserDTO user = userClient.getUserById(notification.getUserId());
                if (user != null && user.getEmail() != null) {
                    emailDestino = user.getEmail(); // ¡Rescatamos el correo real!
                }
            }

            System.out.println("\n==================================================");
            System.out.println("🔔 [NOTIFICATION-SERVICE] PROCESANDO ALERTA...");
            System.out.println("📩 Destino: " + emailDestino);
            System.out.println("📌 Asunto: " + notification.getSubject());
            System.out.println("📝 Mensaje: " + notification.getMessage());
            System.out.println("==================================================\n");

            notification.setStatus("SENT");

        } catch (Exception e) {
            System.out.println("❌ ERROR: Falló el envío de la notificación - " + e.getMessage());
            notification.setStatus("FAILED");
        }

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findByTeamId(Long teamId) {
        return notificationRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findByTournamentId(Long tournamentId) {
        return notificationRepository.findByTournamentId(tournamentId);
    }
}