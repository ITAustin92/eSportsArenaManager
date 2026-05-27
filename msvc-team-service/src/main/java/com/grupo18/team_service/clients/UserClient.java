package com.grupo18.team_service.clients;


import com.grupo18.team_service.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name="user-service", url = "http://localhost:8000/api/v1/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable Long id);

}