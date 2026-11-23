package com.grupo18.prize_service.clients;

import com.grupo18.prize_service.models.dtos.RankingDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
	contextId = "prize-RankingClient",
	name = "msvc-ranking-service",
	url = "${servicios.ranking-url:http://localhost:8007}/api/v1/rankings"
)
public interface RankingClient {
	@GetMapping("/tournament/{tournamentId}")
	List<RankingDTO> getTournamentLeaderboard(@PathVariable("tournamentId") Long tournamentId);
}
