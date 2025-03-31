package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "matriculas")
public class Matricula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estado_matricula_id")
    private EstadoMatricula estadoMatriculaId;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudianteId;

    private Date fechaMatriculacion;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupoId;
    
}
