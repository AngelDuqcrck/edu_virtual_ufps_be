package com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.MateriaOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id.MateriaOracleId;

public interface MateriaOracleRepository extends JpaRepository<MateriaOracle, MateriaOracleId> {
    List<MateriaOracle> findByCodCarrera(String codCarrera);
}
