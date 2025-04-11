package com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.GrupoOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id.GrupoOracleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoOracleRespository extends JpaRepository<GrupoOracle, GrupoOracleId> {
    List<GrupoOracle> findByCodCarrera(String codCarrera);
}
