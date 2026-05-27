package com.grupo18.prize_service.clients;

import com.grupo18.prize_service.models.dtos.RankingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "msvc-ranking-service", url = "http://localhost:8007/api/v1/rankings")
public interface RankingClient {

    /**

     Obtiene la tabla de posiciones ya ordenada (de mayor a menor puntaje)
     para saber exactamente a quién entregarle el primer, segundo y tercer premio.*/@GetMapping("/tournament/{tournamentId}")
    List<RankingDTO> getTournamentLeaderboard(@PathVariable("tournamentId") Long tournamentId);
}