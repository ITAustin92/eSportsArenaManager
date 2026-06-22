package com.grupo18.sanction_service.controllers;

import com.grupo18.sanction_service.models.Sanction;
import com.grupo18.sanction_service.services.SanctionService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sanctions V1", description = "Sanciones a jugadores y equipos")
@RestController
@RequestMapping("/api/v1/sanctions")
public class SanctionController {

    @Autowired
    private SanctionService sanctionService;


    @Operation(summary = "Crear recurso")
    @PostMapping
    public ResponseEntity<Sanction> save(@Valid @RequestBody Sanction sanction) {
        Sanction savedSanction = sanctionService.save(sanction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSanction);
    }


    @Operation(summary = "Operación GET")
    @GetMapping
    public ResponseEntity<List<Sanction>> findAll() {
        return ResponseEntity.ok(sanctionService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Sanction> findById(@PathVariable Long id) {
        return ResponseEntity.ok(sanctionService.findById(id));
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<Sanction> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(sanctionService.updateStatus(id, status));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        sanctionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Sanction>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(sanctionService.findByTournamentId(tournamentId));
    }


    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Sanction>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(sanctionService.findByTeamId(teamId));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Sanction>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(sanctionService.findByUserId(userId));
    }
}