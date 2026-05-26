package com.grupo18.registration_service.clients;

import com.grupo18.registration_service.models.dtos.TeamDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Apuntamos al puerto 8003 donde vive el team-service
@FeignClient(name = "msvc-team-service", url = "http://localhost::8002/api/v1/teams")
public interface TeamClient {

    @GetMapping("/{id}")
    TeamDTO getTeamById(@PathVariable Long id);
}