package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;

public interface CohorteGrupoRepository extends JpaRepository<CohorteGrupo, Integer> {
    
    List<CohorteGrupo> findAllByCohorteId(Cohorte cohorte);
    void deleteAll(Iterable<? extends CohorteGrupo> entities);
}
