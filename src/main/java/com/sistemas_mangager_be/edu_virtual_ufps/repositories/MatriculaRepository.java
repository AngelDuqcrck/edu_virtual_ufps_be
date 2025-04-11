package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoMatricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;

public interface MatriculaRepository extends JpaRepository <Matricula, Long> {

    List<Matricula> findByEstudianteIdAndGrupoCohorteId(Estudiante estudiante, GrupoCohorte grupoCohorte);
    boolean existsByEstudianteIdAndGrupoCohorteId(Estudiante estudiante, GrupoCohorte grupoCohorte);
    List<Matricula> findByEstudianteIdAndEstadoMatriculaId_Id(Estudiante estudiante, Integer estadoMatriculaId);
    //boolean existsByEstudianteIdAndAndGrupoCohorteIdAndEstadoMatriculaId_Id(Estudiante estudiante, GrupoCohorte grupoCohorte, String estadoMatriculaId);
}
