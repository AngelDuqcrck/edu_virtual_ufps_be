package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoEstudiante;

public interface EstadoEstudianteRepository extends JpaRepository<EstadoEstudiante, Integer> {
    
}
