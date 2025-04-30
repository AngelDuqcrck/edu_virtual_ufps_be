package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoEstudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;

import java.util.List;
import java.util.Optional;


public interface EstudianteRepository extends JpaRepository <Estudiante, Integer> {
    
    List<Estudiante> findByPensumId(Pensum pensumId);

    List<Estudiante> findByCohorteId(CohorteGrupo cohorteId);

    List<Estudiante> findByProgramaId(Programa programaId);

    List<Estudiante> findByEstadoEstudianteId(EstadoEstudiante estadoEstudianteId);

    List<Estudiante> findByEstadoEstudianteId_IdAndCohorteId(Integer estadoEstudianteId, CohorteGrupo cohorteId);
    Optional<Estudiante> findByCodigo(String codigo);

}
