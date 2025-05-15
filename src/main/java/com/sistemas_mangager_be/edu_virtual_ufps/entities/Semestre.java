package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;
import jakarta.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "semestres")
public class Semestre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    private Integer numero;

    private String numeroRomano;
}
