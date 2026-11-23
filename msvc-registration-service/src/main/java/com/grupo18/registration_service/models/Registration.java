package com.grupo18.registration_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
	name = "registrations",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = { "team_id", "tournament_id" }),
		@UniqueConstraint(columnNames = { "user_id", "tournament_id" }),
	}
)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Registration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "registration_id")
	private Long registrationId;

	@Column(name = "team_id")
	private Long teamId;

	@Column(name = "user_id")
	private Long userId;

	@NotNull(message = "The tournament ID cannot be null")
	@Column(name = "tournament_id", nullable = false)
	private Long tournamentId;

	@NotNull(message = "The registration date cannot be null")
	@Column(name = "registration_date", nullable = false)
	private LocalDate registrationDate;

	@NotBlank(message = "The registration status cannot be empty")
	@Column(nullable = false)
	private String status;

	@Embedded
	private Audit audit = new Audit();
}
