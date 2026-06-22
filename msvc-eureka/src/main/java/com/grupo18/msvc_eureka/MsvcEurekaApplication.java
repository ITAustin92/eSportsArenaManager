package com.grupo18.msvc_eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// @EnableEurekaServer: convierte esta aplicación en el servidor de descubrimiento.
// Los demás microservicios se registran aquí y se buscan entre sí por su nombre.
@EnableEurekaServer
@SpringBootApplication
public class MsvcEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsvcEurekaApplication.class, args);
    }
}
