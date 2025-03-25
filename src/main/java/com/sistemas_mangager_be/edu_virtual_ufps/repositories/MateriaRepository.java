package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;




public interface MateriaRepository extends JpaRepository<Materia, Integer> {
    
    List<Materia> findByPensumId(Pensum pensumId);
}
