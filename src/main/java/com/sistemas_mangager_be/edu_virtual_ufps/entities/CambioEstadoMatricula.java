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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambioEstadoMatricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matriculaId;

    @ManyToOne
    @JoinColumn(name = "estado_matricula_id")
    private EstadoMatricula estadoMatriculaId;

    private Date fechaCambioEstado;

    private String usuarioCambioEstado;

    private String semestre;
}
