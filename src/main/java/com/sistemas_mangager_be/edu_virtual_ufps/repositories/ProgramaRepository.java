package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import java.util.Optional;


public interface ProgramaRepository extends JpaRepository<Programa, Integer> {
    
    Optional<Programa> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByMoodleId(String moodleId);

    boolean existsByHistoricoMoodleId(String historicoMoodleId);
}
