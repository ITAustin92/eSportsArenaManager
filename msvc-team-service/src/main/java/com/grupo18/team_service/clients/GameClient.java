package com.grupo18.team_service.clients;

import com.grupo18.team_service.models.dtos.GameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Apuntamos directo al puerto 8002 y a la ruta en inglés de los juegos
@FeignClient(name="game-service", url = "http://localhost:8001/api/v1/games")
public interface GameClient {

    // Este método es calcado al estilo de tu profesor.
    // Cuando lo usemos, Feign irá a la URL de arriba, le pegará el ID al final y traerá los datos.
    @GetMapping("/{id}")
    GameDTO getGameById(@PathVariable Long id);
}