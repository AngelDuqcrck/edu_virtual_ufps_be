package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
     @Query("SELECT m FROM Matricula m WHERE m.id = :matriculaId AND m.estudianteId = :estudiante")
    Optional<Matricula> findByIdAndEstudianteId(@Param("matriculaId") Long matriculaId, 
                                              @Param("estudiante") Estudiante estudiante);
    
    List<Solicitud> findByEstudianteId(Estudiante estudiante);
    
    List<Solicitud> findByTipoSolicitudId_Id(Integer tipoSolicitudId);
    
}
