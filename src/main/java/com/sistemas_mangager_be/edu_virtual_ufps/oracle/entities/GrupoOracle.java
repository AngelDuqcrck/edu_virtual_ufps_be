package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id.GrupoOracleId;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@IdClass(GrupoOracleId.class)
@Table(name = "GRUPO")
public class GrupoOracle {

    @Id
    @Column(name = "COD_CARRERA")
    private String codCarrera;

    @Id
    @Column(name = "COD_MATERIA")
    private String codMateria;

    @Id
    @Column(name = "GRUPO")
    private String grupo;

    @Id
    @Column(name = "CICLO")
    private String ciclo;

    @Column(name = "NUM_ALUM_MATRICULADOS")
    private Integer numAlumMatriculados;

    @Column(name = "NUM_MAX_ALUMNOS")
    private Integer numMaxAlumnos;

    @Column(name = "COD_PROFESOR")
    private String codProfesor;

    @Column(name = "NOTAS_PROCESADAS")
    private String notasProcesadas;

    @Column(name = "CEDIDO")
    private String cedido;

    @Column(name = "SECCIONAL")
    private String seccional;

    @Column(name = "DIRIGIDO")
    private String dirigido;
}
