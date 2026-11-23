package com.grupo18.prize_service.models.dtos;

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
public class TournamentDTO {

	private Long tournamentId;

	@com.fasterxml.jackson.annotation.JsonAlias("nombre")
	private String name;

	@com.fasterxml.jackson.annotation.JsonAlias("estado")
	private String state;
}
