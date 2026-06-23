package com.grupo18.notification_service.controllers;

import com.grupo18.notification_service.models.Notification;
import com.grupo18.notification_service.models.dtos.NotificationRequestDTO;
import com.grupo18.notification_service.services.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notifications V1", description = "Notificaciones internas")
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notificaciones V1", description = "Metodos CRUD para la gestión de notificaciones")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        Notification savedNotification = notificationService.processNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }


    @GetMapping
    @Operation(
            summary = "Listado de todas las notificaciones",
            description = "Se devuelve una lista con todas las notificaciones en la tabla notificacion de la DB"
    )
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de una notificación por id",
            description = "Se devuelve una notificación, en caso contrario se devuelve una excepcion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificacíón encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no se encuentra en la BD")
    })
    public ResponseEntity<Notification> findById(@Parameter(description = "ID", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }


    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Busqueda de notificaciones por usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<List<Notification>> findByUserId(@Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findByUserId(userId));
    }


    @GetMapping("/team/{teamId}")
    @Operation(
            summary = "Busqueda de notificaciones por equipo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo encontrado"),
            @ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD")
    })
    public ResponseEntity<List<Notification>> findByTeamId(@Parameter(description = "ID del equipo", required = true, example = "1") @PathVariable Long teamId) {
        return ResponseEntity.ok(notificationService.findByTeamId(teamId));
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(
            summary = "Busqueda de notificaciones por torneo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo encontrado"),
            @ApiResponse(responseCode = "404", description = "Torneo no se encuentra en la BD")
    })
    public ResponseEntity<List<Notification>> findByTournamentId(@Parameter(description = "ID del torneo", required = true, example = "1") @PathVariable Long tournamentId) {
        return ResponseEntity.ok(notificationService.findByTournamentId(tournamentId));
    }
}