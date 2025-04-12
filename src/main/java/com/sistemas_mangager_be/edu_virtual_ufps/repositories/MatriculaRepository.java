package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatriculaRepository extends JpaRepository <Matricula, Long> {

     // Método existente mejorado
    @Query("SELECT m FROM Matricula m " +
           "WHERE m.estudianteId = :estudiante " +
           "AND m.grupoCohorteId.grupoId.materiaId = :materia " +
           "AND m.estadoMatriculaId.id IN :estados")
    List<Matricula> findByEstudianteAndMateriaAndEstados(
            @Param("estudiante") Estudiante estudiante,
            @Param("materia") Materia materia,
            @Param("estados") List<Integer> estados);

    // Método alternativo para verificación rápida
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM Matricula m " +
           "WHERE m.estudianteId = :estudiante " +
           "AND m.grupoCohorteId.grupoId.materiaId = :materia " +
           "AND m.estadoMatriculaId.id IN (1, 2)") // 1=Aprobado, 2=En curso
    boolean existsByEstudianteAndMateriaWithActiveStatus(
            @Param("estudiante") Estudiante estudiante,
            @Param("materia") Materia materia);

    List<Matricula> findByEstudianteIdAndGrupoCohorteId(Estudiante estudiante, GrupoCohorte grupoCohorte);
    
    boolean existsByEstudianteIdAndGrupoCohorteId(Estudiante estudiante, GrupoCohorte grupoCohorte);
    List<Matricula> findByEstudianteIdAndEstadoMatriculaId_Id(Estudiante estudiante, Integer estadoMatriculaId);

    List<Matricula> findByEstudianteId(Estudiante estudiante);
    //boolean existsByEstudianteIdAndAndGrupoCohorteIdAndEstadoMatriculaId_Id(Estudiante estudiante, GrupoCohorte grupoCohorte, String estadoMatriculaId);
}
