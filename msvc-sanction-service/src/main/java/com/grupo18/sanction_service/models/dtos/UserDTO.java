package com.grupo18.sanction_service.models.dtos;

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

	@com.fasterxml.jackson.annotation.JsonAlias("nombre")
	private String name;

	private String lastName;

	@com.fasterxml.jackson.annotation.JsonAlias("rol")
	private String role;
}
