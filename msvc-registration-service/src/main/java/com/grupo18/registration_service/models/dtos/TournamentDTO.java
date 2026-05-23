package com.grupo18.registration_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TournamentDTO {

    private Long tournamentId; // El ID que viene del tournament-service
    private String name;
    private String state; // Vital para la regla de negocio: no inscribir en torneos "FINISHED" o "CANCELED"
    private LocalDate startDate; // Por si necesitamos validar que el torneo aún no ha comenzado

}