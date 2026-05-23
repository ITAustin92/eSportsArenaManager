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

    // --- REGLAS DE NEGOCIO: CONTEXTO Y ASIGNACIÓN ---

    @NotNull(message = "El ID del torneo es obligatorio")
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    // Nota: Es nulo cuando inicia el torneo. Se llena cuando hay un ganador.
    @Column(name = "team_id")
    private Long teamId;

    // --- REGLAS DE NEGOCIO: DETALLES DEL PREMIO ---

    @NotBlank(message = "La descripción del premio es obligatoria")
    @Column(nullable = false)
    private String description; // Ej: "Copa de Campeón", "Premio en Efectivo 1er Lugar"

    @NotBlank(message = "Debe especificar el tipo de premio (ej: MONEY, TROPHY, MEDALS)")
    @Column(nullable = false)
    private String type;

    @Min(value = 0, message = "El monto del premio no puede ser negativo")
    @Column(nullable = false)
    private Double amount = 0.0; // Si es un trofeo físico, el monto puede quedar en 0.

    @NotBlank(message = "El estado del premio es obligatorio")
    @Column(nullable = false)
    private String status = "PENDING"; // Ej: "PENDING" (Esperando ganador), "DELIVERED" (Entregado)

    // --- AUDITORÍA ---
    @Embedded
    private Audit audit = new Audit();
}
