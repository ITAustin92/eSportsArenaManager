package com.grupo18.game_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "juegos")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juego_id")
    private Long juegoId;

    @NotBlank(message = "El campo nombre no puede ser vacio")
    @Column(nullable = false, unique = true) // Regla de negocio: Nombre único
    private String nombre;

    @NotBlank(message = "El campo genero no puede ser vacio")
    @Column(nullable = false)
    private String genero; // Ejemplo: "Shooter", "MOBA", "Deportes"

    @NotBlank(message = "El campo modalidad no puede ser vacio")
    @Column(nullable = false)
    private String modalidad; // Ejemplo: "5v5", "1v1", "Battle Royale"

    @NotNull(message = "El campo jugadores por equipo no puede ser nulo")
    @Positive(message = "La cantidad de jugadores por equipo debe ser un numero positivo") // Regla de negocio: Positivo
    @Column(name = "jugadores_por_equipo", nullable = false)
    private Integer jugadoresPorEquipo;

    @NotBlank(message = "El campo estado no puede ser vacio")
    @Column(nullable = false)
    private String estado; // Ejemplo: "ACTIVO", "INACTIVO"

    // La misma estructura de auditoría incrustada del profe
    @Embedded
    private Audit audit = new Audit();
}