package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ActividadDto {
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime hora;
    private LocalTime horaFin;
    private String lugar;
    private Boolean asistenciaConfirmada;
}
