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

    List<Solicitud> findByEstudianteIdAndAndTipoSolicitudId_Id(Estudiante estudiante, Integer tipoSolicitudId);

    @Query("SELECT s FROM Solicitud s " +
            "WHERE s.estudianteId = :estudiante " +
            "AND s.tipoSolicitudId.id = :tipoSolicitudId " +
            "AND s.estaAprobada = true " +
            "ORDER BY s.fechaAprobacion DESC")
    Optional<Solicitud> findFirstByEstudianteIdAndTipoSolicitudId_IdAndEstaAprobadaTrueOrderByFechaAprobacionDesc(
            @Param("estudiante") Estudiante estudiante,
            @Param("tipoSolicitudId") Integer tipoSolicitudId);

            @Query("SELECT s FROM Solicitud s " +
            "WHERE s.estudianteId = :estudiante " +
            "AND s.tipoSolicitudId.id = 2 " + // 2 = Aplazamiento
            "AND s.estaAprobada = true " +
            "ORDER BY s.fechaAprobacion DESC")
     Optional<Solicitud> findLastAplazamientoAprobadoByEstudiante(
         @Param("estudiante") Estudiante estudiante
     );
 
     // Verifica si un estudiante tiene al menos un aplazamiento aprobado (para validar reintegro)
     @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Solicitud s " +
            "WHERE s.estudianteId = :estudiante " +
            "AND s.tipoSolicitudId.id = 2 " + // 2 = Aplazamiento
            "AND s.estaAprobada = true")
     boolean existsAplazamientoAprobadoByEstudiante(
         @Param("estudiante") Estudiante estudiante
     );
}
