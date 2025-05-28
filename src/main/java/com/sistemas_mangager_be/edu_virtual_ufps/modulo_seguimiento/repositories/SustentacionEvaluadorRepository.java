package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.SustentacionEvaluadorId;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionEvaluador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SustentacionEvaluadorRepository extends JpaRepository<SustentacionEvaluador, SustentacionEvaluadorId> {

    void deleteByIdUsuarioAndIdSustentacion(Integer idUsuario, Integer idSustentacion);

    boolean existsByIdUsuarioAndIdSustentacion(Integer idUsuario, Integer idSustentacion);

    List<SustentacionEvaluador> findByIdSustentacion(Integer idSustentacion);

    SustentacionEvaluador findByIdUsuarioAndIdSustentacion(Integer idUsuario, Integer idSustentacion);

    void deleteByIdSustentacion(Integer idSustentacion);

    List<SustentacionEvaluador> findByIdUsuario(Integer idUsuario);
}