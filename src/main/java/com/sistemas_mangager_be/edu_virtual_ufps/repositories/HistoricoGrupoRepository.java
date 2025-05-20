package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistoricoSemestre;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistoricoGrupo;

import java.util.List;

public interface HistoricoGrupoRepository extends JpaRepository<HistoricoGrupo, Long> {

    List<HistoricoGrupo> findByHistoricoSemestre(HistoricoSemestre historicoSemestre);

    List<HistoricoGrupo> findByGrupoCohorte(GrupoCohorte grupoCohorte);

    boolean existsByGrupoCohorteAndHistoricoSemestre(GrupoCohorte grupoCohorte, HistoricoSemestre historicoSemestre);
}
