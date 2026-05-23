package com.grupo18.game_service.controllers;

import com.grupo18.game_service.models.Game;
import com.grupo18.game_service.services.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@Validated
public class GameController {

    @Autowired
    private GameService gameService;

    // Cumple el requisito: "Listar juegos activos"
    // Al llamarlo desde Postman sin enviarle estado, por defecto traerÃ¡ los "ACTIVO"
    @GetMapping
    public ResponseEntity<List<Game>> findActivos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameService.findByEstado("ACTIVO"));
    }

    // Endpoint adicional por si deseas buscar por otros estados (ej: INACTIVO)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Game>> findByEstado(@PathVariable String estado) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameService.findByEstado(estado));
    }

    // Cumple el requisito: "Buscar juego por ID"
    @GetMapping("/{id}")
    public ResponseEntity<Game> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameService.findById(id));
    }

    // Cumple el requisito: "Crear juego"
    @PostMapping
    public ResponseEntity<Game> save(@Valid @RequestBody Game juego) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.save(juego));
    }

    // Cumple el requisito: "Actualizar modalidad o reglas generales"
    @PutMapping("/{id}")
    public ResponseEntity<Game> update(@PathVariable Long id, @Valid @RequestBody Game juego) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameService.updateById(id, juego));
    }

    // Cumple el requisito: "Desactivar juego"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}