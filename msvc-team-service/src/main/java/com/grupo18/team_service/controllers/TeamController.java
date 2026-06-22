package com.grupo18.team_service.controllers;

import com.grupo18.team_service.models.Team;
import com.grupo18.team_service.services.TeamService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Teams V1", description = "CRUD de equipos y miembros")
@RestController
@RequestMapping("/api/v1/teams")
@Validated
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Operation(summary = "Operación GET")
    @GetMapping
    public ResponseEntity<List<Team>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findAll());
    }


    @Operation(summary = "Crear recurso")
    @PostMapping
    public ResponseEntity<Team> save(@Valid @RequestBody Team equipo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teamService.save(equipo));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Team> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findById(id));
    }


    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<Team>> findByJuego(@PathVariable Long juegoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByJuegoPrincipalId(juegoId));
    }


    @GetMapping("/capitan/{capitanId}")
    public ResponseEntity<List<Team>> findByCapitan(@PathVariable Long capitanId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByCapitanId(capitanId));
    }


    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Team>> findByEstado(@PathVariable String estado) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByEstado(estado));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable Long id, @Valid @RequestBody Team equipo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.updateById(id, equipo));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}