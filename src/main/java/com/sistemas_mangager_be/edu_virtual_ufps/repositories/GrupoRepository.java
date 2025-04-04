package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Grupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;

import java.util.List;


public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    
    List<Grupo> findByDocenteId(Usuario docenteId);

    List<Grupo> findByCohorteId(Cohorte cohorteId);

    List<Grupo> findByMateriaId(Materia materiaId);
}
