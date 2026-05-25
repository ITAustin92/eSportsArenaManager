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

    // Conexión lógica con el match-service
    @NotNull(message = "The match ID cannot be null")
    @Column(name = "match_id", nullable = false, unique = true)
    private Long matchId;

    // El ganador del encuentro
    @NotNull(message = "The winner team ID cannot be null")
    @Column(name = "winner_team_id", nullable = false)
    private Long winnerTeamId;

    // Puntajes finales (opcional, para registro histórico)
    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    // Estado del reporte (ej: "CONFIRMED", "PENDING_VERIFICATION")
    @Column(nullable = false)
    private String status;

    @Embedded
    private Audit audit = new Audit();
}