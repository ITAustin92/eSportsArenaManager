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

    @GetMapping
    public ResponseEntity<List<Tournament>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findAll());
    }


    @PostMapping
    public ResponseEntity<Tournament> save(@Valid @RequestBody Tournament tournament) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tournamentService.save(tournament));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Tournament> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findById(id));
    }


    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Tournament>> findByGameId(@PathVariable Long gameId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByGameId(gameId));
    }


    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Tournament>> findByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByOrganizerId(organizerId));
    }


    @GetMapping("/state/{state}")
    public ResponseEntity<List<Tournament>> findByState(@PathVariable String state) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.findByState(state));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable Long id, @Valid @RequestBody Tournament tournament) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentService.updateById(id, tournament));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}