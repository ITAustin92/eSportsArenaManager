package com.grupo18.team_service.controllers;

import com.grupo18.team_service.models.Team;
import com.grupo18.team_service.services.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@Validated
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public ResponseEntity<List<Team>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findAll());
    }

    // Cumple el requisito: "Crear equipo"
    @PostMapping
    public ResponseEntity<Team> save(@Valid @RequestBody Team equipo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teamService.save(equipo));
    }

    // Cumple el requisito: "Buscar equipo por ID"
    @GetMapping("/{id}")
    public ResponseEntity<Team> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findById(id));
    }

    // Cumple el requisito: "Listar equipos por juego"
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<Team>> findByJuego(@PathVariable Long juegoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByJuegoPrincipalId(juegoId));
    }

    // Cumple el requisito: "Listar equipos por capitán"
    @GetMapping("/capitan/{capitanId}")
    public ResponseEntity<List<Team>> findByCapitan(@PathVariable Long capitanId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByCapitanId(capitanId));
    }

    // Cumple el requisito: "Listar equipos por estado" (ej: traer los ACTIVO o INACTIVO)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Team>> findByEstado(@PathVariable String estado) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findByEstado(estado));
    }

    // Cumple el requisito: "Actualizar nombre, capitán o integrantes"
    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable Long id, @Valid @RequestBody Team equipo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.updateById(id, equipo));
    }

    // Cumple el requisito: "Desactivar equipo"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}