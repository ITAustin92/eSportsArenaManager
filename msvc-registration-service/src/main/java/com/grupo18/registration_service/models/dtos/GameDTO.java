package com.grupo18.registration_service.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDTO {

	private Long juegoId;
	private String estado;
	private String modalidad;
	private Integer jugadoresPorEquipo;
}
