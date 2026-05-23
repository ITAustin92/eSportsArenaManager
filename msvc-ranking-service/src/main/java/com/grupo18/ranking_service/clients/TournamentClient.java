package com.grupo18.ranking_service.clients;

import com.grupo18.ranking_service.models.dtos.TournamentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Conexión con el servicio de torneos en el puerto 8003
@FeignClient(name = "tournament-service", url = "localhost:8003/api/v1/tournaments")
public interface TournamentClient {

    // Método para obtener los datos del torneo (y revisar su estado)
    @GetMapping("/{id}")
    TournamentDTO getTournamentById(@PathVariable Long id);
}