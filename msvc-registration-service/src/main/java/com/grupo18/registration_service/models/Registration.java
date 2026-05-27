package com.grupo18.registration_service.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "registrations")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long registrationId;


    @NotNull(message = "The team ID cannot be null")
    @Column(name = "team_id", nullable = false)
    private Long teamId;


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