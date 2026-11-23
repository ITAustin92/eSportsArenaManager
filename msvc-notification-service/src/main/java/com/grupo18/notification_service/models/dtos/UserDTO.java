package com.grupo18.notification_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	@com.fasterxml.jackson.annotation.JsonAlias("usuarioId")
	private Long id;

	@com.fasterxml.jackson.annotation.JsonAlias("nombre")
	private String name;

	private String lastName;
	private String email;
}
