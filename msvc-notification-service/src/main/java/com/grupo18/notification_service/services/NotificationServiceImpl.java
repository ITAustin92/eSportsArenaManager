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

        // 1. Desempacar el DTO y crear la entidad Notification para la base de datos
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTeamId(request.getTeamId());
        notification.setTournamentId(request.getTournamentId());
        notification.setType(request.getType() != null ? request.getType() : "SYSTEM_ALERT");
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setStatus("PENDING"); // Iniciamos asumiendo que está en cola

        // Guardamos el registro inicial en la base de datos para tener la trazabilidad
        notification = notificationRepository.save(notification);

        try {
            // 2. REGLA DE NEGOCIO: Rescatar el Email y Simular el Envío
            String emailDestino = "alertas_equipo@torneo.com"; // Correo por defecto si el mensaje es grupal (teamId)

            // Si viene con un userId específico, le preguntamos el correo al user-service
            if (notification.getUserId() != null) {
                UserDTO user = userClient.getUserById(notification.getUserId());
                if (user != null && user.getEmail() != null) {
                    emailDestino = user.getEmail(); // ¡Rescatamos el correo real!
                }
            }

            // --- INICIO DE SIMULACIÓN (Esto se verá en tu terminal de Java) ---
            System.out.println("\n==================================================");
            System.out.println("🔔 [NOTIFICATION-SERVICE] PROCESANDO ALERTA...");
            System.out.println("📩 Destino: " + emailDestino);
            System.out.println("📌 Asunto: " + notification.getSubject());
            System.out.println("📝 Mensaje: " + notification.getMessage());
            System.out.println("==================================================\n");
            // --- FIN DE SIMULACIÓN ---

            // 3. Si no hubo errores, cambiamos el estado a enviado
            notification.setStatus("SENT");

        } catch (Exception e) {
            // Si el user-service está apagado o falla la red, atrapamos el error
            System.out.println("❌ ERROR: Falló el envío de la notificación - " + e.getMessage());
            notification.setStatus("FAILED");
        }

        // 4. Actualizamos la notificación en la base de datos con su estado final
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