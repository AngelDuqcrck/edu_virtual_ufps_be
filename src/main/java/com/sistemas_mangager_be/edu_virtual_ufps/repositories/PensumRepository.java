package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;

public interface PensumRepository extends JpaRepository<Pensum, Integer> {
    
    public List<Pensum> findByProgramaId(Programa programaId);
}
