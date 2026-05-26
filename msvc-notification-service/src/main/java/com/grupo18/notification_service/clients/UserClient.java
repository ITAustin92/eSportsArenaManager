package com.grupo18.notification_service.clients;

import com.grupo18.notification_service.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Nos conectamos al microservicio de usuarios (Puerto 8001)
@FeignClient(name = "msvc-user-service", url = "http://localhost::8000/api/v1/users")
public interface UserClient {

    /**

     Trae los datos del usuario (incluyendo su email)
     para poder simular el envío del correo electrónico.*/@GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}