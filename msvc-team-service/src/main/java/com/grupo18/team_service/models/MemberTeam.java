package com.grupo18.team_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "miembros_equipo")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "miembro_id")
    private Long miembroId;

    @NotNull(message = "El campo usuario id no puede ser nulo")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotBlank(message = "El campo rol dentro del equipo no puede ser vacio")
    @Column(name = "rol_dentro_equipo", nullable = false)
    private String rolDentroEquipo; // Ejemplo: "Soporte", "Tirador", "Estratega"
}