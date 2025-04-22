package com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.EstudianteOracle;

import java.util.List;

public interface EstudianteOracleRepository extends JpaRepository<EstudianteOracle, String> {
    List<EstudianteOracle> findByNomCarrera(String nomCarrera);

    List<EstudianteOracle> findByCodigoStartingWith(String codigo);

}