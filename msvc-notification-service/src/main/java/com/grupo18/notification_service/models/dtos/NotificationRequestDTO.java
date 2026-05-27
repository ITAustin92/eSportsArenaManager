package com.grupo18.notification_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    // ¿A quién va dirigido? (Pueden ir nulos dependiendo del evento)
    private Long userId;
    private Long teamId;
    private Long tournamentId;

    // ¿Qué dice el mensaje?
    private String type; // Ej: "EMAIL", "SYSTEM_ALERT"
    private String subject;
    private String message;

}