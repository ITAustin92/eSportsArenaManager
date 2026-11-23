package com.grupo18.registration_service.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SanctionDTO {

	private Long tournamentId;
	private String status;
	private String type;
}
