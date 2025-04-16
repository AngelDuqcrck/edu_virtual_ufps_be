package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface GrupoCohorteRepository extends JpaRepository<GrupoCohorte, Long> {
    
    List<GrupoCohorte> findByCohorteId(Cohorte cohorteId);

    List<GrupoCohorte> findByDocenteId(Usuario docenteId);

    List<GrupoCohorte> findByGrupoId(Grupo grupoId);

    List<GrupoCohorte> findByGrupoId_MateriaId(Materia materiaId);

      @Query("SELECT gc FROM GrupoCohorte gc " +
           "LEFT JOIN FETCH gc.grupoId g " +
           "LEFT JOIN FETCH g.materiaId " +
           "LEFT JOIN FETCH gc.docenteId " +
           "LEFT JOIN FETCH gc.cohorteGrupoId " +
           "LEFT JOIN FETCH gc.cohorteId " +
           "WHERE g.materiaId.id = :materiaId")
    List<GrupoCohorte> findByMateriaIdWithRelations(@Param("materiaId") Integer materiaId);

    

}
