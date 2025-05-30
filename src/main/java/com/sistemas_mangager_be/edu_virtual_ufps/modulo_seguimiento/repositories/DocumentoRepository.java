package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Documento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
    @Query("SELECT d FROM Documento d WHERE d.proyecto.id = :idProyecto AND (:tipoDocumento IS NULL OR d.tipoDocumento = :tipoDocumento)")
    List<Documento> findByProyectoIdAndOptionalTipoDocumento(@Param("idProyecto") Integer idProyecto,
                                                             @Param("tipoDocumento") TipoDocumento tipoDocumento);

}