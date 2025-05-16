package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Semestre;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.SemestrePensum;
import java.util.List;
import java.util.Optional;


public interface SemestrePensumRepository extends JpaRepository<SemestrePensum, Integer> {
    List<SemestrePensum> findByPensumId(Pensum pensumId);
    List<SemestrePensum> findByPensumIdAndSemestreId(Pensum pensumId, Semestre semestreId);

    boolean existsByPensumIdAndId(Pensum pensumId, Integer id);
    Optional<SemestrePensum> findFirstByPensumIdAndSemestreId_Numero(Pensum pensumId, Integer numero);
    Optional<SemestrePensum> findFirstByPensumIdAndSemestreId_NumeroRomano(Pensum pensumId, String numero);
}
