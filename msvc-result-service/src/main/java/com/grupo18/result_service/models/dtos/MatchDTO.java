package com.grupo18.result_service.models.dtos;

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
	private Long homeUserId;
	private Long awayTeamId;
	private Long awayUserId;
	private String status;
	private java.time.LocalDateTime matchDate;
	private String round;
	private Integer homeScore;
	private Integer awayScore;
}
