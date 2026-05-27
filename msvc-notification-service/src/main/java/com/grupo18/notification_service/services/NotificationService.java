package com.grupo18.notification_service.services;

import com.grupo18.notification_service.models.Notification;
import com.grupo18.notification_service.models.dtos.NotificationRequestDTO;

import java.util.List;

public interface NotificationService {

    // MÉTODO PRINCIPAL: Recibe la orden de otro microservicio y procesa el envío
    Notification processNotification(NotificationRequestDTO request);

    // CRUD de Lectura
    Notification findById(Long id);
    List<Notification> findAll();

    // Filtros de historial
    List<Notification> findByUserId(Long userId);
    List<Notification> findByTeamId(Long teamId);
    List<Notification> findByTournamentId(Long tournamentId);
}