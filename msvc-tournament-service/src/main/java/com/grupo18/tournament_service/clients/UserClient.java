package com.grupo18.tournament_service.clients;

import com.grupo18.tournament_service.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Nos conectamos al puerto 8000 donde corre el servicio de usuarios
@FeignClient(name = "msvc-user-service", url = "http://localhost::8000/api/v1/users")
public interface UserClient {

    // Tocamos la puerta del GET por ID del usuario
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable Long id);
}