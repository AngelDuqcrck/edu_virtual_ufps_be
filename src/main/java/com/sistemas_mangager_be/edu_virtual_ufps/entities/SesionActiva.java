package com.sistemas_mangager_be.edu_virtual_ufps.entities;


import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sesiones_activas")
public class SesionActiva {
    
    @Id
    @Column(length = 100)
    private String correoUsuario;
    
    @Column(columnDefinition = "TEXT")
    private String token;
    
    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;
    
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    public SesionActiva() {}
    
    public SesionActiva(String correoUsuario, String token, LocalDateTime fechaExpiracion) {
        this.correoUsuario = correoUsuario;
        this.token = token;
        this.ultimaActividad = LocalDateTime.now();
        this.fechaExpiracion = fechaExpiracion;
    }
}