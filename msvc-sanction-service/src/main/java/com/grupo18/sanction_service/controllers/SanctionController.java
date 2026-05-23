package com.grupo18.sanction_service.controllers;

import com.grupo18.sanction_service.models.Sanction;
import com.grupo18.sanction_service.services.SanctionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sanctions")
public class SanctionController {

    @Autowired
    private SanctionService sanctionService;

    // --- 1. CRUD BÁSICO ---

    // CREAR: Guardar una nueva sanción (Aplica reglas de negocio en el Service)
    @PostMapping
    public ResponseEntity<Sanction> save(@Valid @RequestBody Sanction sanction) {
        Sanction savedSanction = sanctionService.save(sanction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSanction);
    }

    // LEER TODOS: Traer todo el historial de sanciones del sistema
    @GetMapping
    public ResponseEntity<List<Sanction>> findAll() {
        return ResponseEntity.ok(sanctionService.findAll());
    }

    // LEER UNO: Buscar una sanción por su ID exacto
    @GetMapping("/{id}")
    public ResponseEntity<Sanction> findById(@PathVariable Long id) {
        return ResponseEntity.ok(sanctionService.findById(id));
    }

    // ACTUALIZAR ESTADO: Útil para apelaciones (cambiar a "APPEALED", "SERVED", etc.)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Sanction> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(sanctionService.updateStatus(id, status));
    }

    // BORRAR: Eliminar una sanción (si fue un error del árbitro, por ejemplo)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        sanctionService.deleteById(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 sin contenido
    }

    // --- 2. FILTROS POR REGLA DE NEGOCIO (Contexto) ---

    // HISTORIAL DEL TORNEO: Ver todas las sanciones de un campeonato
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Sanction>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(sanctionService.findByTournamentId(tournamentId));
    }

    // HISTORIAL DEL EQUIPO: Ver todas las faltas de un equipo
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Sanction>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(sanctionService.findByTeamId(teamId));
    }

    // HISTORIAL DEL USUARIO: Ver el registro disciplinario de una persona (jugador/DT)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Sanction>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(sanctionService.findByUserId(userId));
    }
}