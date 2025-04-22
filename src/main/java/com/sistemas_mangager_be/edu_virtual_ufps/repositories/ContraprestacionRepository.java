package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Contraprestacion;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.TipoContraprestacion;

import java.util.List;


public interface ContraprestacionRepository extends JpaRepository<Contraprestacion, Integer> {
    
    List<Contraprestacion> findByEstudianteId(Estudiante estudianteId);

    List<Contraprestacion> findByTipoContraprestacionId(TipoContraprestacion tipoContraprestacionId);

    boolean existsByEstudianteIdAndSemestre(Estudiante estudiante, String semestre);
}
