package com.grupo18.tournament_service.controllers;

import com.grupo18.tournament_service.models.Tournament;
import com.grupo18.tournament_service.services.TournamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
@Validated
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // Cumple el requisito: Crear torneo (gatillará la validación de juego y organizador vía Feign)
    @PostMapping
    public ResponseEntity<Tournament> save(@Valid @RequestBody Tournament tournament) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tournamentService.save(tournament));
    }

    // Cumple el requisito: Buscar torneo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findById(id));
    }

    // Ruta semántica: Listar torneos por juego
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Tournament>> findByGameId(@PathVariable Long gameId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByGameId(gameId));
    }

    // Ruta semántica: Listar torneos por organizador
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Tournament>> findByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByOrganizerId(organizerId));
    }

    // Ruta semántica: Listar torneos por estado (ej: UPCOMING, FINISHED)
    @GetMapping("/state/{state}")
    public ResponseEntity<List<Tournament>> findByState(@PathVariable String state) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByState(state));
    }

    // Cumple el requisito: Actualizar torneo
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable Long id, @Valid @RequestBody Tournament tournament) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.updateById(id, tournament));
    }

    // Cumple el requisito: Desactivación lógica del torneo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna un 204 sin cuerpo
    }
}