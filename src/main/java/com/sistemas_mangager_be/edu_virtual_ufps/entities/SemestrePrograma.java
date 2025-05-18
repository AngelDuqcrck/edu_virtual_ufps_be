package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemestrePrograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "semestre_id")
    private Semestre semestre;

    @ManyToOne
    @JoinColumn(name = "programa_id")
    private Programa programa;

    private String moodleId;

    

}
