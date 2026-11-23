package com.grupo18.ranking_service.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class MatchResultUpdateDTO {

	@NotNull
	private Long matchId;

	@NotNull
	private Long tournamentId;

	private Long winnerTeamId;
	private Long loserTeamId;
	private Long winnerUserId;
	private Long loserUserId;
	private boolean active = true;
}
