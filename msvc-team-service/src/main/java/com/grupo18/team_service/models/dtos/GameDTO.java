package com.grupo18.team_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GameDTO {

	@com.fasterxml.jackson.annotation.JsonAlias("juegoId")
	private Long gameId;

	@com.fasterxml.jackson.annotation.JsonAlias("nombre")
	private String name;

	@com.fasterxml.jackson.annotation.JsonAlias("estado")
	private String state;

	private String estado;
	private String modalidad;
	private Integer jugadoresPorEquipo;
}
