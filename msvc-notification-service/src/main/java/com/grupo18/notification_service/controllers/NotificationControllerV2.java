package com.grupo18.notification_service.controllers;

import com.grupo18.notification_service.assemblers.NotificationModelAssembler;
import com.grupo18.notification_service.models.Notification;
import com.grupo18.notification_service.models.dtos.NotificationRequestDTO;
import com.grupo18.notification_service.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// V2 — HATEOAS. Crear notificación dispara el envío (processNotification), no es un save genérico.
@RestController
@RequestMapping("/api/v2/notifications")
@Validated
@Tag(name = "Notifications V2", description = "Notificaciones internas con respuestas HATEOAS")
public class NotificationControllerV2 {

    @Autowired private NotificationService notificationService;
    @Autowired private NotificationModelAssembler notificationModelAssembler;

    @GetMapping
    @Operation(summary = "Listar notificaciones")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Notification>>> findAll() {
        List<EntityModel<Notification>> models = notificationService.findAll().stream().map(notificationModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(NotificationControllerV2.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificación por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encontrada"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    public ResponseEntity<EntityModel<Notification>> findById(
            @Parameter(description = "ID", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificationModelAssembler.toModel(notificationService.findById(id)));
    }

    @PostMapping("/send")
    @Operation(summary = "Enviar notificación")
    public ResponseEntity<EntityModel<Notification>> send(@Valid @RequestBody NotificationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationModelAssembler.toModel(notificationService.processNotification(request)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Notificaciones de un usuario")
    public ResponseEntity<CollectionModel<EntityModel<Notification>>> findByUser(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long userId) {
        List<EntityModel<Notification>> models = notificationService.findByUserId(userId).stream().map(notificationModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(NotificationControllerV2.class).findByUser(userId)).withSelfRel()));
    }
}
