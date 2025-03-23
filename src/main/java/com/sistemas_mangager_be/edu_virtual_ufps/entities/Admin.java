package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.*;
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

    private String segundoNombre;

    @NotEmpty
    @Column(nullable = false)
    private String primerApellido;

    private String segundoApellido;
    
    private String email;
    
    private String password;

    private Boolean esSuperAdmin;
}
