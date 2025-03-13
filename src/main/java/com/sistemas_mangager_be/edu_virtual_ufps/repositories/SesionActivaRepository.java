package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.SesionActiva;

public interface SesionActivaRepository extends JpaRepository<SesionActiva, String> {
    Optional<SesionActiva> findByCorreoUsuario(String correoUsuario);
    
    Optional<SesionActiva> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM SesionActiva s WHERE s.fechaExpiracion < ?1")
    void eliminarSesionesExpiradas(LocalDateTime fecha);
    
    @Modifying
    @Query("UPDATE SesionActiva s SET s.ultimaActividad = ?2 WHERE s.correoUsuario = ?1")
    void actualizarUltimaActividad(String correoUsuario, LocalDateTime ultimaActividad);
}
