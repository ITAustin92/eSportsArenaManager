package com.grupo18.sanction_service.clients;

import com.grupo18.sanction_service.models.dtos.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Apunta al puerto 8010, que es donde vive nuestro notification-service
@FeignClient(name = "notification-service", url = "localhost:8010/api/v1/notifications")
public interface NotificationClient {

    /**

     Envía la orden a la central de mensajería para que dispare un correo o alerta.*/@PostMapping("/send")
    void sendNotification(@RequestBody NotificationRequestDTO request);
}