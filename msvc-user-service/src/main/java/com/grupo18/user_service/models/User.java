package com.grupo18.user_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="usuarios")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "nombre_completo", nullable = false)
    @NotBlank(message = "El campo nombre completo no puede ser vacio")
    private String nombreCompleto;

    @Column(name = "nickname", nullable = false)
    @NotBlank(message = "El campo nickname completo no puede ser vacio")
    private String nickname;

    @NotBlank(message = "El campo de email no puede ser vacio")
    @Email(message = "El campo de email tiene que tener el formato de correo")
    @Column(nullable = false, unique = true)
    private String email;
}
