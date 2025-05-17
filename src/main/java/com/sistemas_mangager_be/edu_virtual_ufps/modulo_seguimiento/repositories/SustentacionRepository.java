package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SustentacionRepository extends JpaRepository<Sustentacion, Integer> {
    Optional<Sustentacion> findByProyectoId(Integer idProyecto);
}