package com.grupo18.registration_service.clients;

import com.grupo18.registration_service.models.dtos.SanctionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
	name = "msvc-sanction-service",
	url = "${servicios.sanction-url:http://localhost:8008}/api/v1/sanctions"
)
public interface SanctionClient {
	@GetMapping("/team/{id}")
	java.util.List<SanctionDTO> porEquipo(@PathVariable Long id);

	@GetMapping("/user/{id}")
	java.util.List<SanctionDTO> porUsuario(@PathVariable Long id);
}
