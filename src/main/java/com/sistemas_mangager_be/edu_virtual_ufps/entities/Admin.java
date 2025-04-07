package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@Table(name = "admins")
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(nullable = false)
    private String primerNombre;

    @Column(nullable = true) // Puede ser nulo
    private String segundoNombre;

    @NotEmpty
    @Column(nullable = false)
    private String primerApellido;

    @Column(nullable = true) // Puede ser nulo
    private String segundoApellido;

    @Email
    @Column(nullable = false, unique = true) // No nulo y único
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean esSuperAdmin;

    private Boolean activo;
}