package com.grupo18.sanction_service.clients;

import com.grupo18.sanction_service.models.dtos.TournamentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-tournament-service", url = "http://localhost:8003/api/v1/tournaments")
public interface TournamentClient {

    // Trae los datos del torneo para verificar si está activo o finalizado
    @GetMapping("/{id}")
    TournamentDTO getTournamentById(@PathVariable("id") Long id);
}