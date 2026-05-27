package com.grupo18.match_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Apuntamos al puerto 8004 donde vive el registration-service
@FeignClient(name = "msvc-registration-service", url = "http://localhost:8004/api/v1/registrations")
public interface RegistrationClient {

    @GetMapping("/exists")
    boolean isTeamRegisteredInTournament(@RequestParam Long teamId, @RequestParam Long tournamentId);
}