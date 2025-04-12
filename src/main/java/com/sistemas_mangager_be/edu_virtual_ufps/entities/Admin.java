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

    
    @Column(name = "primer_nombre", nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre", nullable = true)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido", nullable = true)
    private String segundoApellido;

    @Column(name = "es_super_admin", nullable = false)
    private Boolean esSuperAdmin;

    @Email
    @Column(nullable = false, unique = true) // No nulo y único
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String password;

    private Boolean activo;
}