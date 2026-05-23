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

    // --- REGLAS DE NEGOCIO: DESTINATARIOS Y CONTEXTO ---

    // Nota: userId puede ser nulo si el mensaje es para todo un equipo
    @Column(name = "user_id")
    private Long userId;

    // Nota: teamId puede ser nulo si el mensaje es privado para un solo usuario
    @Column(name = "team_id")
    private Long teamId;

    // Opcional: Sirve para saber de qué campeonato proviene la alerta
    @Column(name = "tournament_id")
    private Long tournamentId;

    // --- REGLAS DE NEGOCIO: CONTENIDO DEL MENSAJE ---

    @NotBlank(message = "Debe especificar el canal de envío (ej: EMAIL, SMS, PUSH)")
    @Column(nullable = false)
    private String type;

    @NotBlank(message = "El asunto o título es obligatorio")
    @Column(nullable = false)
    private String subject; // Ej: "Aviso de Suspensión", "Cambio de Horario"

    @NotBlank(message = "El cuerpo del mensaje no puede estar vacío")
    @Column(nullable = false, length = 1500) // Se le da más espacio (1500 caracteres) para textos largos
    private String message;

    @NotBlank(message = "El estado de la notificación es obligatorio")
    @Column(nullable = false)
    private String status = "PENDING"; // Ej: "PENDING" (En cola), "SENT" (Enviado), "FAILED" (Error al enviar)

    // --- AUDITORÍA ---
    @Embedded
    private Audit audit = new Audit();
}