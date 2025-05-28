package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.SustentacionDocumentoId;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SustentacionDocumentoRepository extends JpaRepository<SustentacionDocumento, SustentacionDocumentoId> {

    void deleteByIdSustentacionAndIdDocumento(Integer idSustentacion, Integer idDocumento);

    boolean existsByIdSustentacionAndIdDocumento(Integer idSustentacion, Integer idDocumento);

    List<SustentacionDocumento> findByIdSustentacion(Integer idSustentacion);

    void deleteByIdSustentacion(Integer idSustentacion);

    @Query("SELECT s.proyecto.id FROM Sustentacion s WHERE s.id = :idSustentacion")
    Optional<Integer> findProyectoIdBySustentacionId(@Param("idSustentacion") Integer idSustentacion);
}