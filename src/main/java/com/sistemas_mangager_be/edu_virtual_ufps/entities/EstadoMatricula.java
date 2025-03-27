package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "estados_matriculas")
public class EstadoMatricula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    
}
