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

    // Cumple el requisito: "Listar todas las inscripciones"
    @GetMapping
    public ResponseEntity<List<Registration>> findAll() {
        return ResponseEntity.ok(registrationService.findAll());
    }

    // Crear una nueva inscripción
    // Cumple la regla: Se validará en el Service que el equipo y torneo sean válidos
    @PostMapping
    public ResponseEntity<Registration> save(@Valid @RequestBody Registration registration) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.save(registration));
    }

    // Cumple el requisito: "Actualizar estado de inscripción"
    @PatchMapping("/{id}/status")
    public ResponseEntity<Registration> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(registrationService.updateStatus(id, status));
    }

    // Buscar una inscripción específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<Registration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.findById(id));
    }

    // Ruta semántica: Buscar todas las inscripciones de un equipo
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Registration>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(registrationService.findByTeamId(teamId));
    }

    // Ruta semántica: Buscar todos los equipos inscritos en un torneo
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Registration>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(registrationService.findByTournamentId(tournamentId));
    }

    // Cumple el requisito: "Cancelar inscripción"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        registrationService.cancelById(id);
        return ResponseEntity.noContent().build();
    }
}