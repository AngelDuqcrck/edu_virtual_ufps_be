package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.LineaInvestigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaInvestigacionRepository extends JpaRepository<LineaInvestigacion, Integer> {
    List<LineaInvestigacion> findByGrupoInvestigacionId(Integer grupoId);
}