package com.sistemas_mangager_be.edu_virtual_ufps.repositoriesOracle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemas_mangager_be.edu_virtual_ufps.entitiesOracle.EstudianteVista;


@Repository
public interface EstudianteVistaRepository extends JpaRepository<EstudianteVista, Long> {
}