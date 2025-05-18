package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

        // Método existente mejorado
        @Query("SELECT m FROM Matricula m " +
                        "WHERE m.estudianteId = :estudiante " +
                        "AND m.grupoCohorteId.grupoId.materiaId = :materia " +
                        "AND m.estadoMatriculaId.id IN :estados")
        List<Matricula> findByEstudianteAndMateriaAndEstados(
                        @Param("estudiante") Estudiante estudiante,
                        @Param("materia") Materia materia,
                        @Param("estados") List<Integer> estados);

        @Query("SELECT m FROM Matricula m" +
                        " WHERE m.estudianteId = :estudiante" +
                        " AND m.semestre = :semestre" +
                        " AND m.estadoMatriculaId.id IN (1, 2, 4)") // 1=Aprobado, 2=En curso 4=Reprobado
        List<Matricula> findByEstudianteAndSemestreAndEstados(
                        @Param("estudiante") Estudiante estudiante,
                        @Param("semestre") String semestre);

        // Método alternativo para verificación rápida
        @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
                        "FROM Matricula m " +
                        "WHERE m.estudianteId = :estudiante " +
                        "AND m.grupoCohorteId.grupoId.materiaId = :materia " +
                        "AND m.estadoMatriculaId.id IN (1, 2)") // 1=Aprobado, 2=En curso
        boolean existsByEstudianteAndMateriaWithActiveStatus(
                        @Param("estudiante") Estudiante estudiante,
                        @Param("materia") Materia materia);

        @Query("SELECT m FROM Matricula m WHERE m.id = :matriculaId AND m.estudianteId = :estudiante AND m.estadoMatriculaId.id = 2")
        Optional<Matricula> findActiveByIdAndEstudianteId(@Param("matriculaId") Long matriculaId,
                        @Param("estudiante") Estudiante estudiante);

        List<Matricula> findByEstudianteIdAndGrupoCohorteId(Estudiante estudiante, GrupoCohorte grupoCohorte);

        boolean existsByEstudianteIdAndGrupoCohorteId(Estudiante estudiante, GrupoCohorte grupoCohorte);

        List<Matricula> findByEstudianteIdAndEstadoMatriculaId_Id(Estudiante estudiante, Integer estadoMatriculaId);

        List<Matricula> findByEstudianteId(Estudiante estudiante);

        List<Matricula> findByEstudianteIdAndEstadoMatriculaId_IdAndSemestre(Estudiante estudiante,
                        Integer estadoMatriculaId, String semestre);

        @Query("SELECT m FROM Matricula m WHERE m.semestre = :semestre AND m.grupoCohorteId = :grupoCohorte AND m.estadoMatriculaId.id IN (1, 2, 4)")
        List<Matricula> findBySemestreAndGrupoCohorteIdAndEstados(String semestre, GrupoCohorte grupoCohorte);
        List<Matricula> findByGrupoCohorteIdAndEstadoMatriculaId(GrupoCohorte grupoCohorte, EstadoMatricula estado);

        boolean existsByEstudianteIdAndEstadoMatriculaId_IdAndCorreoEnviado(
                        @Param("estudianteId") Estudiante estudiante,
                        @Param("estadoMatriculaId") Integer estadoMatriculaId,
                        @Param("correoEnviado") boolean correoEnviado);

        // boolean
        // existsByEstudianteIdAndAndGrupoCohorteIdAndEstadoMatriculaId_Id(Estudiante
        // estudiante, GrupoCohorte grupoCohorte, String estadoMatriculaId);

        @Query("SELECT DISTINCT m.estudianteId FROM Matricula m WHERE m.grupoCohorteId = :grupoCohorte AND m.estadoMatriculaId.id = :estadoId")
        List<Estudiante> findEstudiantesByGrupoCohorteIdAndEstadoMatriculaId(
                        @Param("grupoCohorte") GrupoCohorte grupoCohorte,
                        @Param("estadoId") Integer estadoId);

        @Query("SELECT DISTINCT m.estudianteId FROM Matricula m WHERE m.semestre = :semestre AND m.grupoCohorteId = :grupoCohorte AND m.estadoMatriculaId.id IN (1, 2, 4)") // 1=Aprobado,
                                                                                                                                                 // 2=En
                                                                                                                                                 // curso,
                                                                                                                                                 // 4=Reprobado
        List<Estudiante> findEstudiantesBySemestreAndGrupoCohorteIdAndEstados(
                        @Param("grupoCohorte") GrupoCohorte grupoCohorte,
                        @Param("semestre") String semestre);



        /*
         * @Query("SELECT m FROM Matricula m" +
         * " WHERE m.estudianteId = :estudiante" +
         * " AND m.semestre = :semestre" +
         * " AND m.estadoMatriculaId.id IN (1, 2, 4)") // 1=Aprobado, 2=En curso
         * 4=Reprobado
         * List<Matricula> findByEstudianteAndSemestreAndEstados(
         * 
         * @Param("estudiante") Estudiante estudiante,
         * 
         * @Param("semestre") String semestre);
         */
        List<Matricula> findByGrupoCohorteIdAndEstadoMatriculaId_Id(GrupoCohorte grupoCohorte,
                        Integer estadoMatriculaId);

        List<Matricula> findByGrupoCohorteId(GrupoCohorte grupoCohorteId);
}
