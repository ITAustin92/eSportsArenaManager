package com.grupo18.user_service.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    @NotBlank(message = "El campo de nombre no puede ser vacio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El campo de nickname no puede ser vacio")
    @Column(nullable = false, unique = true)
    private String nickname;

    @NotBlank(message = "El campo de correo no puede ser vacio")
    @Email(message = "El campo de correo tiene que tener el formato de correo")
    @Column(nullable = false, unique = true)
    private String correo;

    @NotBlank(message = "El campo de rol no puede ser vacio")
    @Column(nullable = false)
    private String rol;

    @NotBlank(message = "El campo de estado no puede estar vacio")
    @Column(nullable = false)
    private String estado; // Puede ser "ACTIVO", "INACTIVO" o "SANCIONADO"

    // ¡Aquí está la forma exacta del profesor!
    @Embedded
    Audit audit = new Audit();
}
