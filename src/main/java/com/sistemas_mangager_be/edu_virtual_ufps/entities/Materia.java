package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "materias")
public class Materia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@Column(nullable = false, unique = true)
    private String codigo;

    private String nombre;

    private String creditos;

    @ManyToOne
    @JoinColumn(name = "pensum_id")
    private Pensum pensumId;

    private String semestre;

    @ManyToOne
    @JoinColumn(name = "semestre_pensum_id")
    private SemestrePensum semestrePensum;

    private String moodleId;

}
