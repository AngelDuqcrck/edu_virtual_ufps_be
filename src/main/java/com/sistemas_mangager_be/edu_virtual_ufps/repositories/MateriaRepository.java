package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    List<Materia> findByPensumId(Pensum pensumId);

    Optional<Materia> findByCodigo(String codigo);
    List<Materia> findByPensumIdOrderBySemestreAscCodigoAsc(Pensum pensum);

    @Query("SELECT m FROM Materia m " +
            "JOIN Grupo g ON g.materiaId = m " +
            "JOIN GrupoCohorte gc ON gc.grupoId = g " +
            "JOIN Matricula mat ON mat.grupoCohorteId = gc " +
            "WHERE mat.estudianteId.id = :estudianteId")
    List<Materia> findAllByPensumId_Estudiante(@Param("estudianteId") Integer estudianteId);
}
