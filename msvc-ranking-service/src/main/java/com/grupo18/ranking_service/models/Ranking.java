package com.grupo18.ranking_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "rankings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tournament_id", "team_id"})
})
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ranking_id")
    private Long rankingId;

    @NotNull(message = "El ID del torneo no puede ser nulo")
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @NotNull(message = "El ID del equipo no puede ser nulo")
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Min(value = 0, message = "Los puntos no pueden ser negativos")
    @Column(nullable = false)
    private Integer points = 0; // Por defecto empiezan en 0

    @Min(value = 0, message = "Los partidos jugados no pueden ser negativos")
    @Column(name = "matches_played", nullable = false)
    private Integer matchesPlayed = 0;

    @Min(value = 0, message = "Las victorias no pueden ser negativas")
    @Column(nullable = false)
    private Integer wins = 0;

    @Min(value = 0, message = "Las derrotas no pueden ser negativas")
    @Column(nullable = false)
    private Integer losses = 0;

    @Embedded
    private Audit audit = new Audit();
}