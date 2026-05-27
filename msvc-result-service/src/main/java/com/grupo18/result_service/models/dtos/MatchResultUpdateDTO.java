package com.grupo18.result_service.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultUpdateDTO {

    @NotNull(message = "El torneo es obligatorio para actualizar el ranking")
    private Long tournamentId;

    @NotNull(message = "El equipo ganador es obligatorio")
    private Long winnerTeamId;


    @NotNull(message = "El equipo perdedor es obligatorio")
    private Long loserTeamId;

}