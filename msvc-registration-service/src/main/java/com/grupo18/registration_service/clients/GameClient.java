package com.grupo18.registration_service.clients;

import com.grupo18.registration_service.models.dtos.GameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
	contextId = "registration-GameClient",
	name = "msvc-game-service",
	url = "${servicios.game-url:http://localhost:8001}/api/v1/games"
)
public interface GameClient {
	@GetMapping("/{id}")
	GameDTO obtener(@PathVariable Long id);
}
