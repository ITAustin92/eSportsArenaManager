package com.grupo18.prize_service.clients;

import com.grupo18.prize_service.models.dtos.TournamentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-tournament-service", url = "http://localhost::8003/api/v1/tournaments")
public interface TournamentClient {

    /**

     Trae los datos del torneo para que el servicio verifique
     si el estado es "FINISHED" antes de soltar los premios.*/@GetMapping("/{id}")
    TournamentDTO getTournamentById(@PathVariable("id") Long id);
}