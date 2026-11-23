package com.grupo18.match_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TournamentDTO {

	private Long tournamentId;

	@com.fasterxml.jackson.annotation.JsonAlias("nombre")
	private String name;

	@com.fasterxml.jackson.annotation.JsonAlias("estado")
	private String state;
}
