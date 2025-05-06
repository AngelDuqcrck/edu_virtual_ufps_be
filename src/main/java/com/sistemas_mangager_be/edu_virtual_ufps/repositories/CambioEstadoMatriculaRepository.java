package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.CambioEstadoMatricula;

public interface CambioEstadoMatriculaRepository extends JpaRepository<CambioEstadoMatricula, Long> {

    List<CambioEstadoMatricula> findByMatriculaId_EstudianteIdAndSemestre(Estudiante estudianteId, String semestre);
    
}
