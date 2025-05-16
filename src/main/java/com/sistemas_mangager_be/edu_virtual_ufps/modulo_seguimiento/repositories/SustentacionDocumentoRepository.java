package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.SustentacionDocumentoId;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SustentacionDocumentoRepository extends JpaRepository<SustentacionDocumento, SustentacionDocumentoId> {

    void deleteByIdSustentacionAndIdDocumento(Integer idSustentacion, Integer idDocumento);

    boolean existsByIdSustentacionAndIdDocumento(Integer idSustentacion, Integer idDocumento);

    List<SustentacionDocumento> findByIdSustentacion(Integer idSustentacion);

    void deleteByIdSustentacion(Integer idSustentacion);

}