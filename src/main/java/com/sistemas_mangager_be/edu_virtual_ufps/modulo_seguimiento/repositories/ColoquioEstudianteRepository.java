package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.ColoquioEstudianteId;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.ColoquioEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColoquioEstudianteRepository extends JpaRepository<ColoquioEstudiante, ColoquioEstudianteId> {
    List<ColoquioEstudiante> findByIdColoquioAndIdEstudiante(Integer idColoquio, Integer idEstudiante);
}