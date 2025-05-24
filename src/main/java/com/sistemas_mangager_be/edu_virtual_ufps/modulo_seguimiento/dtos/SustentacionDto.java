package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for {@link Sustentacion}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SustentacionDto implements Serializable {
    Integer id;
    String tipoSustentacion;
    LocalDate fecha;
    LocalTime hora;
    LocalTime horaFin;
    String lugar;
    String descripcion;
    Boolean asistenciaConfirmada;
    Boolean sustentacionExterna;
    Boolean sustentacionRealizada;
    Integer idProyecto ;
    List<SustentacionEvaluadorDto> evaluadores;
    List<CriterioEvaluacionDto> criteriosEvaluacion;
}