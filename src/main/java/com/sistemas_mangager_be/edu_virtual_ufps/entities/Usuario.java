package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
    private String nombre;
    
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    

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
