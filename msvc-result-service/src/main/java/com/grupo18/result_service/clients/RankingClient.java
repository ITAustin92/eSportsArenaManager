package com.grupo18.result_service.clients;

import com.grupo18.result_service.models.dtos.MatchResultUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Apuntamos al ranking-service en el puerto 8007
@FeignClient(name = "msvc-ranking-service", url = "http://localhost:8007/api/v1/rankings")
public interface RankingClient {

    @PostMapping("/update")
    void updateRanking(@RequestBody MatchResultUpdateDTO updateDTO);
}