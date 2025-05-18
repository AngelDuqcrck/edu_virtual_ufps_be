package com.sistemas_mangager_be.edu_virtual_ufps.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historico_grupos")
public class HistoricoGrupo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grupo_cohorte_id", nullable = false)
    private GrupoCohorte grupoCohorte;

    @ManyToOne
    @JoinColumn(name = "historico_semestre_id", nullable = false)
    private HistoricoSemestre historicoSemestre;

    @Column(name = "moodle_curso_original_id")
    private String moodleCursoOriginalId;

    @Column(name = "moodle_curso_historico_id")
    private String moodleCursoHistoricoId;

    @Column(name = "fecha_creacion", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();
}