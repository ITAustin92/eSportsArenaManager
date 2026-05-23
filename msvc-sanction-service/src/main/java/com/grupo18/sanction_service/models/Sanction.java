package com.grupo18.sanction_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sanctions")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Sanction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sanction_id")
    private Long sanctionId;

    // --- REGLAS DE NEGOCIO: AISLAMIENTO Y REFERENCIAS ---

    @NotNull(message = "El ID del torneo es obligatorio")
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @NotNull(message = "El ID del equipo es obligatorio")
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    // Nota: matchId puede ser nulo, porque a veces un equipo es sancionado
    // fuera de un partido (ej: problemas administrativos)
    @Column(name = "match_id")
    private Long matchId;

    // Nota: playerId puede ser nulo si la sanción es para todo el equipo
    @Column(name = "user_id")
    private Long userId;

    // --- REGLAS DE NEGOCIO: DETALLES DEL CASTIGO ---

    @NotBlank(message = "Debe especificar el tipo de sanción (ej: RED_CARD, FINE, SUSPENSION)")
    @Column(nullable = false)
    private String type;

    @NotBlank(message = "Debe incluir el motivo o razón de la sanción")
    @Column(nullable = false, length = 500)
    private String reason;

    @Min(value = 0, message = "Los partidos de suspensión no pueden ser negativos")
    @Column(name = "matches_suspended", nullable = false)
    private Integer matchesSuspended = 0;

    @Min(value = 0, message = "El monto de la multa no puede ser negativo")
    @Column(name = "fine_amount", nullable = false)
    private Double fineAmount = 0.0;

    @NotBlank(message = "El estado de la sanción no puede estar vacío")
    @Column(nullable = false)
    private String status = "ACTIVE"; // Ej: "ACTIVE" (Vigente), "SERVED" (Cumplida), "APPEALED" (Apelada)

    // --- AUDITORÍA ---
    @Embedded
    private Audit audit = new Audit();
}