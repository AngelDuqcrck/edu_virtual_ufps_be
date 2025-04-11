package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id.MateriasMatriculadasId;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@IdClass(MateriasMatriculadasId.class)
@Table(name = "MATERIAS_MATRICULADAS")
public class MateriasMatriculadasOracle {

    @Id
    @Column(name = "COD_ALUMNO")
    private String codAlumno;

    @Id
    @Column(name = "COD_CARRERA")
    private String codCarrera;

    @Id
    @Column(name = "COD_MATERIA")
    private String codMateria;

    @Id
    @Column(name = "GRUPO")
    private String grupo;

    @Column(name = "COD_CAR_MAT")
    private String codCarMat;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "COD_MAT_MAT")
    private String codMatMat;

    @Column(name = "SECCIONAL")
    private String seccional;
}
