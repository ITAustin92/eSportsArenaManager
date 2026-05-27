package com.grupo18.result_service.clients;

import com.grupo18.result_service.models.dtos.MatchDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Apuntamos al puerto 8005 donde vive el match-service
@FeignClient(name = "msvc-match-service", url = "http://localhost:8005/api/v1/matches")
public interface MatchClient {

    // Método para validar que el partido existe antes de asignar un resultado
    @GetMapping("/{id}")
    MatchDTO getMatchById(@PathVariable Long id);
}