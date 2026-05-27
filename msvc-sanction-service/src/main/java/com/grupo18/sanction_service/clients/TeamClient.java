package com.grupo18.sanction_service.clients;

import com.grupo18.sanction_service.models.dtos.TeamDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-team-service", url = "http://localhost:8002/api/v1/teams")
public interface TeamClient {

    // Trae los datos del equipo sancionado (nombre)
    @GetMapping("/{id}")
    TeamDTO getTeamById(@PathVariable("id") Long id);
}