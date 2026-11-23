package com.grupo18.registration_service.models.dtos;

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

	@com.fasterxml.jackson.annotation.JsonAlias("estado")
	private String state;

	private LocalDate startDate;
	private Long gameId;
	private Integer maxParticipants;
	private java.time.Instant registrationDeadline;
}
