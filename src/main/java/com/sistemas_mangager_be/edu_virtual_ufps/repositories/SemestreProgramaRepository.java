package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Semestre;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.SemestrePrograma;
import java.util.List;


public interface SemestreProgramaRepository extends JpaRepository<SemestrePrograma, Integer> {

    boolean existsBySemestreAndPrograma(Semestre semestre, Programa programa);

    List<SemestrePrograma> findByPrograma(Programa programa);
    
}
