package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Retroalimentacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetroalimentacionRepository extends JpaRepository<Retroalimentacion, Integer> {
}