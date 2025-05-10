package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistorialCierreNotas;

public interface HistorialCierreNotasRepository extends JpaRepository<HistorialCierreNotas, Long> {
    List<HistorialCierreNotas> findFirstByGrupoCohorteIdOrderByFechaCierreDesc(Long grupoCohorteId);
    void deleteByGrupoCohorteId(Long grupoCohorteId);
    List<HistorialCierreNotas> findByGrupoCohorteId(Long grupoCohorteId);
}