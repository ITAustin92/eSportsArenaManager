package com.grupo18.match_service.controllers;

import com.grupo18.match_service.models.Match;
import com.grupo18.match_service.services.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PutMapping("/{id}")
    public ResponseEntity<Match> update(@PathVariable Long id, @Valid @RequestBody Match match) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(matchService.updateById(id, match));
    }

    // Crear un nuevo partido
    // Se delega al Service toda la validación de inscripciones y torneo
    @PostMapping
    public ResponseEntity<Match> save(@Valid @RequestBody Match match) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matchService.save(match));
    }

    // Buscar partido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Match> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    // Ruta semántica: Listar partidos de un torneo
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Match>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(matchService.findByTournamentId(tournamentId));
    }

    // Ruta semántica: Listar calendario de un equipo (sea local o visitante)
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Match>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(matchService.findByTeamId(teamId));
    }

    // Cumple el requisito: "Cancelar partida"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}