package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistoricoSemestre;

import java.util.List;
import java.util.Optional;

public interface HistoricoSemestreRepository extends JpaRepository<HistoricoSemestre, Integer> {

    List<HistoricoSemestre> findByPrograma(Programa programa);

    Optional<HistoricoSemestre> findByProgramaAndSemestre(Programa programa, String semestre);

    boolean existsByProgramaAndSemestre(Programa programa, String semestre);
}
