package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.CriterioEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Integer> {
    List<CriterioEvaluacion> findBySustentacionId(Integer idSustentacion);
}