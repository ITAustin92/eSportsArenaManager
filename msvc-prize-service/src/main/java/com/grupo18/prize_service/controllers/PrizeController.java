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

    @PostMapping
    public ResponseEntity<Prize> save(@Valid @RequestBody Prize prize) {
        Prize savedPrize = prizeService.save(prize);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPrize);
    }

    @GetMapping
    public ResponseEntity<List<Prize>> findAll() {
        return ResponseEntity.ok(prizeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prize> findById(@PathVariable Long id) {
        return ResponseEntity.ok(prizeService.findById(id));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Prize>> findByTournamentId(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(prizeService.findByTournamentId(tournamentId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Prize>> findByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(prizeService.findByTeamId(teamId));
    }

    @PostMapping("/tournament/{tournamentId}/distribute")
    public ResponseEntity<String> distributePrizes(@PathVariable Long tournamentId) {

        prizeService.distributePrizesForTournament(tournamentId);

        return ResponseEntity.ok("Los premios del torneo " + tournamentId + " han sido distribuidos exitosamente según el ranking.");
    }
}