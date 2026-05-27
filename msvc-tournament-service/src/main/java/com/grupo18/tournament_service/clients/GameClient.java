package com.grupo18.tournament_service.clients;

import com.grupo18.tournament_service.models.dtos.GameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Nos conectamos al puerto 8001 donde corre el servicio de juegos
@FeignClient(name = "msvc-game-service", url = "http://localhost:8001/api/v1/games")
public interface GameClient {

    // Tocamos la puerta del GET por ID del juego
    @GetMapping("/{id}")
    GameDTO getGameById(@PathVariable Long id);
}