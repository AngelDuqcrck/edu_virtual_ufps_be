package com.sistemas_mangager_be.edu_virtual_ufps.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historico_semestres")
public class HistoricoSemestre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;

    @Column(nullable = false, length = 10)
    private String semestre; // Formato "YYYY-I" o "YYYY-II"

    @Column(name = "fecha_inicio")
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    private Date fechaFin;

    @Column(name = "moodle_categoria_id")
    private String moodleCategoriaId;
}