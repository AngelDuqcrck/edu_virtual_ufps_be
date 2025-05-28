package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.ColoquioEstudianteId;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.ColoquioEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ColoquioEstudianteRepository extends JpaRepository<ColoquioEstudiante, ColoquioEstudianteId> {
    List<ColoquioEstudiante> findByIdColoquioAndIdEstudiante(Integer idColoquio, Integer idEstudiante);

    @Query("SELECT DISTINCT ce.idEstudiante FROM ColoquioEstudiante ce WHERE ce.idColoquio = :idColoquio")
    List<Integer> findIdEstudiantesConDocumentoEntregado(@Param("idColoquio") Integer idColoquio);

    boolean existsByColoquioIdAndIdEstudiante(Integer coloquioId, Integer usuarioId);
}