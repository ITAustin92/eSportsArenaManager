package com.grupo18.match_service.models.dtos;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegistrationDTO {

	private Long registrationId;
	private Long teamId;
	private Long tournamentId;
	private LocalDate registrationDate;
	private String status;
}
