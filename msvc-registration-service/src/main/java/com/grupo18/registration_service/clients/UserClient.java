package com.grupo18.registration_service.clients;

import com.grupo18.registration_service.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
	name = "msvc-user-service",
	url = "${servicios.user-url:http://localhost:8000}/api/v1/users"
)
public interface UserClient {
	@GetMapping("/{id}")
	UserDTO obtener(@PathVariable Long id);
}
