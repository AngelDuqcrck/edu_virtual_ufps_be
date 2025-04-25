package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String primerNombre;

    @Column(nullable = true)
    private String segundoNombre;

    @Column(nullable = false)
    private String primerApellido;

    @Column(nullable = true)
    private String segundoApellido;

    // @NotEmpty(message = "La cédula no puede estar vacía")
    @Size(min = 6, max = 12, message = "El numero de documento debe tener entre 6 y 12 dígitos")
    @Column(unique = true)
    private String cedula;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 10, message = "El número de teléfono debe tener 10 digitos")
    @Pattern(regexp = "^[3-6]\\d{9}$", message = "El número de teléfono debe tener 10 digitos y comenzar con 3 o 6")
    private String telefono;

    @Column(unique = true)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rolId;

    @Column(name = "google_id", unique = true)
    private String googleId; // ID único de Google

    @Column(name = "foto_url")
    private String fotoUrl; // URL de la foto de perfil de Google
}
