package com.grupo18.match_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegistrationDTO {

    private Long registrationId;
    private Long teamId;
    private Long tournamentId;
    private LocalDate registrationDate;
    private String status; // Importante: "CONFIRMED" o "PENDING"
}