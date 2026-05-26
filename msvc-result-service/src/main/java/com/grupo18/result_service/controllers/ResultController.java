package com.grupo18.result_service.controllers;

import com.grupo18.result_service.models.Result;
import com.grupo18.result_service.services.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    // Reportar un nuevo resultado
    // Cumple la regla: Se valida en el Service que el partido exista y el ganador sea válido
    @PostMapping
    public ResponseEntity<Result> save(@Valid @RequestBody Result result) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resultService.save(result));
    }

    // Buscar resultado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Result> findById(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.findById(id));
    }

    // Actualizar resultado antes de validación
    @PutMapping("/{id}")
    public ResponseEntity<Result> update(@PathVariable Long id, @Valid @RequestBody Result result) {
        return ResponseEntity.ok(resultService.updateById(id, result));
    }

    // Anular resultado con justificación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar el resultado de un partido específico por el ID del partido
    @GetMapping("/match/{matchId}")
    public ResponseEntity<Result> findByMatchId(@PathVariable Long matchId) {
        return ResponseEntity.ok(resultService.findByMatchId(matchId));
    }

    // Listar todos los partidos ganados por un equipo específico
    // Útil para generar tablas de estadísticas o rankings
    @GetMapping("/winner/{teamId}")
    public ResponseEntity<List<Result>> findByWinnerTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(resultService.findByWinnerTeamId(teamId));
    }
}