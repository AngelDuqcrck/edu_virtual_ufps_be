package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;

import java.util.List;



public interface GrupoCohorteRepository extends JpaRepository<GrupoCohorte, Long> {
    
    List<GrupoCohorte> findByCohorteId(Cohorte cohorteId);

    List<GrupoCohorte> findByDocenteId(Usuario docenteId);

    List<GrupoCohorte> findByGrupoId(Grupo grupoId);

}
