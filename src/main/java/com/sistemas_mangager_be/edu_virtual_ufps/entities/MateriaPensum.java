package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MateriaPensum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materia_id;

    @ManyToOne
    @JoinColumn(name = "pensum_id")
    private Pensum pensum_id;

    private String semestre;
}
