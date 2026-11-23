package com.grupo18.tournament_service.models.dtos;

import java.time.LocalDate;
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

	private Long gameId;
	private LocalDate startDate;
	private LocalDate endDate;

	@com.fasterxml.jackson.annotation.JsonAlias("estado")
	private String state;

	private Long organizerId;
}
