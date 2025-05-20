package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColoquioRepository extends JpaRepository<Coloquio, Integer> {

    List<Coloquio> findByGrupoCohorteId(Long grupoCohorteId);

    @Query("""
    SELECT c FROM Coloquio c
    JOIN c.grupoCohorte gc
    JOIN gc.cohorteGrupoId cg
    JOIN Estudiante e ON e.cohorteId = cg
    WHERE e.usuarioId.id = :usuarioId
    """)
    List<Coloquio> findColoquiosByUsuarioId(Long usuarioId);
}