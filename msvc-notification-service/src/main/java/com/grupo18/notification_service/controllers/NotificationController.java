package com.grupo18.notification_service.controllers;

import com.grupo18.notification_service.models.Notification;
import com.grupo18.notification_service.models.dtos.NotificationRequestDTO;
import com.grupo18.notification_service.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // --- 1. PUERTA DE RECEPCIÓN (Regla de negocio: Motor de eventos) ---

    /**
     * Los otros microservicios (Match, Sanction, Prize, etc.) usan su cliente Feign
     * para enviar el "sobre" (NotificationRequestDTO) a esta ruta exacta.
     */
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        // Le pasamos el sobre al Service para que rescate el email y haga la simulación
        Notification savedNotification = notificationService.processNotification(request);

        // Devolvemos un 201 (CREATED) confirmando que la alerta entró al sistema
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }

    // --- 2. LECTURA Y AUDITORÍA ---

    // VER TODAS: Revisar el historial global de correos de la plataforma
    @GetMapping
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    // VER UNA: Buscar el detalle de una alerta por su ID exacto
    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    // --- 3. FILTROS DE HISTORIAL (Bandejas de entrada) ---

    // BANDEJA DEL USUARIO: Ver todas las alertas enviadas a una persona (ej: "Mis mensajes")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findByUserId(userId));
    }

    // BANDEJA DEL EQUIPO: Ver todos los comunicados enviados a un club
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Notification>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(notificationService.findByTeamId(teamId));
    }

    // ARCHIVO DEL TORNEO: Ver todos los mensajes generados en un campeonato específico
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Notification>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(notificationService.findByTournamentId(tournamentId));
    }
}