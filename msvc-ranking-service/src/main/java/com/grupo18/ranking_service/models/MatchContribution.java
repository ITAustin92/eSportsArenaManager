package com.grupo18.ranking_service.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "match_contributions")
public class MatchContribution {

	@Id
	private Long matchId;

	private Long tournamentId;
	private Long winnerTeamId;
	private Long loserTeamId;
	private Long winnerUserId;
	private Long loserUserId;
	private boolean active;
}
