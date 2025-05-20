package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SustentacionRepository extends JpaRepository<Sustentacion, Integer> {

    Optional<Sustentacion> findByProyectoId(Integer idProyecto);

    List<Sustentacion> findByFecha(LocalDate fecha);

    @Query("""
    SELECT u FROM Usuario u
    JOIN UsuarioProyecto up ON up.usuario = u
    JOIN Proyecto p ON up.proyecto = p
    JOIN Sustentacion s ON s.proyecto = p
    JOIN Rol r ON up.rol = r
    WHERE s.id = :sustentacionId
    AND LOWER(r.nombre) = 'estudiante'
    """)
    List<Usuario> findEstudiantesBySustentacionId(Integer sustentacionId);
}