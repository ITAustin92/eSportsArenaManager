package com.grupo18.team_service.clients;


import com.grupo18.team_service.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Apuntamos al puerto 8001 y a la ruta de los usuarios en inglés
@FeignClient(name="user-service", url = "localhost:8000/api/v1/users")
public interface UserClient {

    // Método calcado al del profe: pide un usuario por ID
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable Long id);

}