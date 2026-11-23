package com.grupo18.registration_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TeamDTO {

	private Long id;

	@com.fasterxml.jackson.annotation.JsonAlias("nombre")
	private String name;

	private String estado;
	// Capitán del equipo (se usa para validar que el jugador inscriba solo sus equipos)
	private Long capitanId;
	private Long juegoPrincipalId;
	private java.util.List<MemberDTO> miembros;

	@Getter
	@Setter
	public static class MemberDTO {

		private Long usuarioId;
	}
}
