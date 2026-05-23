package com.grupo18.ranking_service.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MatchResultUpdateDTO {

    // Necesitamos saber en qué torneo ocurrió para actualizar la tabla correcta
    @NotNull(message = "El torneo es obligatorio para actualizar el ranking")
    private Long tournamentId;

    // Al equipo ganador le sumaremos los puntos base y una victoria
    @NotNull(message = "El equipo ganador es obligatorio")
    private Long winnerTeamId;

    // Al equipo perdedor le sumaremos una derrota y partidos jugados
    @NotNull(message = "El equipo perdedor es obligatorio")
    private Long loserTeamId;

}