package com.grupo18.tournament_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // Asegúrate de importar esto

@SpringBootApplication
@EnableFeignClients // ¡Esta es la llave mágica que soluciona tu error!
public class TournamentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TournamentServiceApplication.class, args);
	}

}
