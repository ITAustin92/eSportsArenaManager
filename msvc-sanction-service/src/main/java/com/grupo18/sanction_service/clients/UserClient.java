package com.grupo18.sanction_service.clients;

import com.grupo18.sanction_service.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-user-service", url = "localhost:8001/api/v1/users")
public interface UserClient {

    // Trae los datos del usuario infractor (nombre, apellido, rol)
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}