package com.sistemas_mangager_be.edu_virtual_ufps.entities;


import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrupoCohorte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupoId;

    @ManyToOne
    @JoinColumn(name = "cohorte_grupo_id")
    private CohorteGrupo cohorteGrupoId;


    @ManyToOne
    @JoinColumn(name = "cohorte_id")
    private Cohorte cohorteId;

    private Date fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Usuario docenteId;

    private String moodleId;

    private String semestre;

    private boolean semestreTerminado; 
}
