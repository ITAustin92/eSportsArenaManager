package com.grupo18.notification_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "tournament_id")
    private Long tournamentId;


    @NotBlank(message = "Debe especificar el canal de envío (ej: EMAIL, SMS, PUSH)")
    @Column(nullable = false)
    private String type;

    @NotBlank(message = "El asunto o título es obligatorio")
    @Column(nullable = false)
    private String subject;

    @NotBlank(message = "El cuerpo del mensaje no puede estar vacío")
    @Column(nullable = false, length = 1500)
    private String message;

    @NotBlank(message = "El estado de la notificación es obligatorio")
    @Column(nullable = false)
    private String status = "PENDING";

    @Embedded
    private Audit audit = new Audit();
}