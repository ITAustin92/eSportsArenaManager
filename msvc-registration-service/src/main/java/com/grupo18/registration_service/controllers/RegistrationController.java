package com.grupo18.registration_service.controllers;

import com.grupo18.registration_service.models.Registration;
import com.grupo18.registration_service.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<Registration>> findAll() {
        return ResponseEntity.ok(registrationService.findAll());
    }

    @PostMapping
    public ResponseEntity<Registration> save(@Valid @RequestBody Registration registration) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.save(registration));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Registration> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(registrationService.updateStatus(id, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.findById(id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Registration>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(registrationService.findByTeamId(teamId));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Registration>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(registrationService.findByTournamentId(tournamentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        registrationService.cancelById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByTeamAndTournament(
            @RequestParam Long teamId,
            @RequestParam Long tournamentId) {
        boolean exists = registrationService.existsByTeamIdAndTournamentId(teamId, tournamentId);
        return ResponseEntity.ok(exists);
    }
}