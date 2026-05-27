package com.grupo18.result_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;


    @NotNull(message = "The match ID cannot be null")
    @Column(name = "match_id", nullable = false, unique = true)
    private Long matchId;


    @NotNull(message = "The winner team ID cannot be null")
    @Column(name = "winner_team_id", nullable = false)
    private Long winnerTeamId;


    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;


    @Column(nullable = false)
    private String status;

    @Embedded
    private Audit audit = new Audit();
}