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


    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        Notification savedNotification = notificationService.processNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }


    @GetMapping
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findByUserId(userId));
    }


    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Notification>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(notificationService.findByTeamId(teamId));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Notification>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(notificationService.findByTournamentId(tournamentId));
    }
}