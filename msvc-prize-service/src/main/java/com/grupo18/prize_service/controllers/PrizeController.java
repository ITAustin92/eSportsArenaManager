package com.grupo18.prize_service.controllers;

import com.grupo18.prize_service.models.Prize;
import com.grupo18.prize_service.services.PrizeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prizes")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    // --- 1. CRUD BÁSICO ---

    // CREAR: Registrar un nuevo premio (ej: "1er Lugar: $5000")
    @PostMapping
    public ResponseEntity<Prize> save(@Valid @RequestBody Prize prize) {
        Prize savedPrize = prizeService.save(prize);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPrize);
    }

    // LEER TODOS: Ver la bóveda completa de todos los premios del sistema
    @GetMapping
    public ResponseEntity<List<Prize>> findAll() {
        return ResponseEntity.ok(prizeService.findAll());
    }

    // LEER UNO: Ver los detalles de un premio específico
    @GetMapping("/{id}")
    public ResponseEntity<Prize> findById(@PathVariable Long id) {
        return ResponseEntity.ok(prizeService.findById(id));
    }

    // --- 2. FILTROS POR CONTEXTO ---

    // BOLSA DE PREMIOS DEL TORNEO: Ver todos los premios en juego de un campeonato
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Prize>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(prizeService.findByTournamentId(tournamentId));
    }

    // VITRINA DEL EQUIPO: Ver todos los premios/trofeos que ha ganado un equipo
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Prize>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(prizeService.findByTeamId(teamId));
    }

    // --- 3. REGLA DE NEGOCIO PRINCIPAL ---

    // BOTÓN DE REPARTO: El administrador llama a esta ruta cuando el torneo termina
    @PostMapping("/tournament/{tournamentId}/distribute")
    public ResponseEntity<String> distributePrizes(@PathVariable Long tournamentId) {

        // Llamamos al motor que cruza los datos con el ranking
        prizeService.distributePrizesForTournament(tournamentId);

        // Devolvemos un mensaje de éxito para que Postman o el Frontend confirmen la acción
        return ResponseEntity.ok("Los premios del torneo " + tournamentId + " han sido distribuidos exitosamente según el ranking.");
    }
}