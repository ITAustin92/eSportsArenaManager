package com.grupo18.tournament_service.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

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
    @FutureOrPresent(message = "The start date must be today or in the future")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "The end date cannot be null")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotBlank(message = "The tournament state cannot be empty")
    @Column(nullable = false)
    private String state;

    @Embedded
    private Audit audit = new Audit();
}