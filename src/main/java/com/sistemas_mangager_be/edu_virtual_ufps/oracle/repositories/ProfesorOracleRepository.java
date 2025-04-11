package com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.ProfesorOracle;

public interface ProfesorOracleRepository extends JpaRepository<ProfesorOracle, String> {
    List<ProfesorOracle> findByCodProfesor(String codProfesor);
}
