package com.grupo18.tournament_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Tournament {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tournament_id")
	private Long tournamentId;

	@NotBlank(message = "The tournament name cannot be empty")
	@Column(nullable = false, unique = true)
	private String name;

	@NotNull(message = "The game ID cannot be null")
	@Column(name = "game_id", nullable = false)
	private Long gameId;

	@NotNull(message = "The organizer ID cannot be null")
	@Column(name = "organizer_id", nullable = false)
	private Long organizerId;

	@NotNull(message = "The start date cannot be null")
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@NotNull(message = "The end date cannot be null")
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@NotBlank(message = "The tournament state cannot be empty")
	@Column(nullable = false)
	private String state;

	@jakarta.validation.constraints.Min(2)
	@Column(name = "max_participants", nullable = false, columnDefinition = "integer default 16")
	private Integer maxParticipants = 16;

	@Column(name = "registration_deadline")
	private java.time.Instant registrationDeadline;

	@Embedded
	private Audit audit = new Audit();

	// API siempre expone un instante UTC; el cierre predeterminado es el inicio en Chile.
	public java.time.Instant getRegistrationDeadline() {
		if (registrationDeadline != null) return registrationDeadline;
		return startDate == null
			? null
			: startDate.atStartOfDay(java.time.ZoneId.of("America/Santiago")).toInstant();
	}
}
