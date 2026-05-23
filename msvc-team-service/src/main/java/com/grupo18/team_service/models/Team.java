package com.grupo18.team_service.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipos")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipo_id")
    private Long equipoId;

    @NotBlank(message = "El campo nombre no puede ser vacio")
    @Column(nullable = false, unique = true)
    private String nombre;

    // Regla de negocio: Un equipo debe tener capitán obligatoriamente
    @NotNull(message = "El campo capitan no puede ser nulo")
    @Column(name = "capitan_id", nullable = false)
    private Long capitanId;

    @NotNull(message = "El campo juego principal no puede ser nulo")
    @Column(name = "juego_principal_id", nullable = false)
    private Long juegoPrincipalId;

    @NotBlank(message = "El campo estado no puede ser vacio")
    @Column(nullable = false)
    private String estado; // Ejemplo: "ACTIVO", "INACTIVO"

    // Relación OneToMany: Un equipo tiene una lista de miembros
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "equipo_id")
    private List<MemberTeam> miembros = new ArrayList<>();

    @Embedded
    private Audit audit = new Audit();
}