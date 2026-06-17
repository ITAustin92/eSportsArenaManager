package com.grupo18.match_service.clients;

import com.grupo18.match_service.models.dtos.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Apunta al puerto 8010, que es donde vive nuestro notification-service
@FeignClient(name = "notification-service", url = "http://localhost:8010/api/v1/notifications")
public interface NotificationClient {

    @PostMapping("/send")
    void sendNotification(@RequestBody NotificationRequestDTO request);
}