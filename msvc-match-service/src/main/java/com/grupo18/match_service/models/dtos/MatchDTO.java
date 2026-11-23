package com.grupo18.match_service.models.dtos;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MatchDTO {

	private Long matchId;
	private Long tournamentId;
	private Long homeTeamId;
	private Long awayTeamId;
	private Integer homeScore;
	private Integer awayScore;
	private LocalDateTime matchDate;
	private String status;
}
