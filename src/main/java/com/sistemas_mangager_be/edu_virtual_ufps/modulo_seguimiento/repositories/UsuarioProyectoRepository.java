package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.UsuarioProyectoId;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.UsuarioProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioProyectoRepository extends JpaRepository<UsuarioProyecto, UsuarioProyectoId> {

    void deleteByIdUsuarioAndIdProyecto(Integer idUsuario, Integer idProyecto);

    boolean existsByIdUsuarioAndIdProyecto(Integer idUsuario, Integer idProyecto);

    List<UsuarioProyecto> findByIdProyecto(Integer idProyecto);

    boolean existsByUsuarioIdAndRolNombre(Integer idUsuario, String nombreRol);

    @Query("""
    SELECT up.proyecto FROM UsuarioProyecto up
    JOIN up.rol r
    WHERE up.usuario.id = :idUsuario AND LOWER(r.nombre) = 'estudiante'
    """)
    Optional<Proyecto> findProyectoByEstudianteId(@Param("idUsuario") Integer idUsuario);

    @Query("""
    SELECT up.proyecto FROM UsuarioProyecto up
    JOIN up.rol r
    JOIN up.proyecto p
    LEFT JOIN p.lineaInvestigacion li
    LEFT JOIN li.grupoInvestigacion gi
    LEFT JOIN gi.programa pr
    WHERE up.usuario.id = :idUsuario
      AND LOWER(r.nombre) IN ('director', 'codirector')
      AND (:lineaId IS NULL OR li.id = :lineaId)
      AND (:grupoId IS NULL OR gi.id = :grupoId)
      AND (:programaId IS NULL OR pr.id = :programaId)
    """)
    List<Proyecto> findProyectosByDocenteDirectorId(@Param("idUsuario") Integer idUsuario,
                                                    @Param("lineaId") Integer lineaId,
                                                    @Param("grupoId") Integer grupoId,
                                                    @Param("programaId") Integer programaId);
}