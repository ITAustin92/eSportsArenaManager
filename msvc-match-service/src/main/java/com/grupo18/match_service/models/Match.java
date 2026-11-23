package com.grupo18.match_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "match_id")
	private Long matchId;

	@NotNull(message = "The tournament ID cannot be null")
	@Column(name = "tournament_id", nullable = false)
	private Long tournamentId;

	@Column(name = "home_team_id")
	private Long homeTeamId;

	private Long homeUserId;

	@Column(name = "away_team_id")
	private Long awayTeamId;

	private Long awayUserId;

	// Resultados
	@Min(value = 0, message = "The score cannot be negative")
	@Column(name = "home_score")
	private Integer homeScore;

	@Min(value = 0, message = "The score cannot be negative")
	@Column(name = "away_score")
	private Integer awayScore;

	@NotNull(message = "The match date cannot be null")
	@Column(name = "match_date", nullable = false)
	private LocalDateTime matchDate;

	@NotBlank(message = "The match status cannot be empty")
	@Column(nullable = false)
	private String status;

	@Column(name = "round_name")
	private String round;

	@Embedded
	private Audit audit = new Audit();
}
