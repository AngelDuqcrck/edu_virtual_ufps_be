package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Table(name = "cohorte_grupos")
@AllArgsConstructor
@NoArgsConstructor
public class CohorteGrupo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "cohorte_id")
    private Cohorte cohorteId;
    
}
