package com.grupo18.tournament_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO {

	@com.fasterxml.jackson.annotation.JsonAlias("usuarioId")
	private Long id;

	private String nickname;

	@com.fasterxml.jackson.annotation.JsonAlias("estado")
	private String state;

	@com.fasterxml.jackson.annotation.JsonAlias("rol")
	private String role;
}
