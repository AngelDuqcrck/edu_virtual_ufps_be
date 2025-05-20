package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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

    List<Coloquio> findByFecha(LocalDate fecha);

    @Query("""
    SELECT u FROM Usuario u
    JOIN Estudiante e ON e.usuarioId = u
    JOIN CohorteGrupo cg ON e.cohorteId = cg
    JOIN GrupoCohorte gc ON gc.cohorteGrupoId = cg
    JOIN Coloquio c ON c.grupoCohorte = gc
    WHERE c.id = :coloquioId
    """)
    List<Usuario> findUsuariosByColoquioId(Integer coloquioId);
}