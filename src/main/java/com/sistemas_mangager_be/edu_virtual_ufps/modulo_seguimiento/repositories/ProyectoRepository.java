package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {
    @Query("""
    SELECT p FROM Proyecto p
    WHERE (:lineaId IS NULL OR p.lineaInvestigacion.id = :lineaId)
    AND (:grupoId IS NULL OR p.lineaInvestigacion.grupoInvestigacion.id = :grupoId)
    AND (:programaId IS NULL OR p.lineaInvestigacion.grupoInvestigacion.programa.id = :programaId)
    """)
    List<Proyecto> findAllByFiltros(
            @Param("lineaId") Integer lineaId,
            @Param("grupoId") Integer grupoId,
            @Param("programaId") Integer programaId);
}