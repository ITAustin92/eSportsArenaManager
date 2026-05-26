package com.grupo18.match_service.clients;

import com.grupo18.match_service.models.dtos.TournamentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Apuntamos al puerto 8003 donde vive el tournament-service
@FeignClient(name = "msvc-tournament-service", url = "http://localhost::8003/api/v1/tournaments")
public interface TournamentClient {

    @GetMapping("/{id}")
    TournamentDTO getTournamentById(@PathVariable Long id);
}