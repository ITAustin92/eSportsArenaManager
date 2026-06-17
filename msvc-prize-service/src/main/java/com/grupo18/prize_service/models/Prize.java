package com.grupo18.prize_service.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "prizes")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Prize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prize_id")
    private Long prizeId;


    @NotNull(message = "El ID del torneo es obligatorio")
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(name = "team_id")
    private Long teamId;

    @NotBlank(message = "La descripción del premio es obligatoria")
    @Column(nullable = false)
    private String description;

    @NotBlank(message = "Debe especificar el tipo de premio (ej: MONEY, TROPHY, MEDALS)")
    @Column(nullable = false)
    private String type;

    @Min(value = 0, message = "El monto del premio no puede ser negativo")
    @Column(nullable = false)
    private Double amount = 0.0;

    @NotBlank(message = "El estado del premio es obligatorio")
    @Column(nullable = false)
    private String status = "PENDING";

    @Embedded
    private Audit audit = new Audit();
}
