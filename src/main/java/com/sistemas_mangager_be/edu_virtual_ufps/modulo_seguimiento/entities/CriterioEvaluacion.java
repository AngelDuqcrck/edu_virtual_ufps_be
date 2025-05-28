package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "criterio_evaluacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriterioEvaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_sustentacion")
    private Sustentacion sustentacion;
}
