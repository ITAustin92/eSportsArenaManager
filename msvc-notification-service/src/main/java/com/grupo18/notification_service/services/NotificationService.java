package com.grupo18.notification_service.services;

import com.grupo18.notification_service.models.Notification;
import com.grupo18.notification_service.models.dtos.NotificationRequestDTO;

import java.util.List;

public interface NotificationService {

    Notification processNotification(NotificationRequestDTO request);

    Notification findById(Long id);
    List<Notification> findAll();

    List<Notification> findByUserId(Long userId);
    List<Notification> findByTeamId(Long teamId);
    List<Notification> findByTournamentId(Long tournamentId);
}