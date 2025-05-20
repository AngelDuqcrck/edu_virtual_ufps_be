package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio}
 */
@Value
public class ColoquioDto implements Serializable {
    Integer id;
    LocalDate fecha;
    LocalTime hora;
    String lugar;
    String descripcion;

    Long grupoCohorteId;
    String grupo;
    String materia;
}