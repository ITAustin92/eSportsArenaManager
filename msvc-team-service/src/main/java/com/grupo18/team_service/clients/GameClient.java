package com.grupo18.team_service.clients;

import com.grupo18.team_service.models.dtos.GameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name="game-service", url = "http://localhost:8001/api/v1/games")
public interface GameClient {

    @GetMapping("/{id}")
    GameDTO getGameById(@PathVariable Long id);
}