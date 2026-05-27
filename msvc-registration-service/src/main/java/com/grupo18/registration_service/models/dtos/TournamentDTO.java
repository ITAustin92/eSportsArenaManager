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

    private Long tournamentId;
    private String name;
    private String state;
    private LocalDate startDate;

}