package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import java.util.Date;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContraprestacionResponse {
    
    private Integer id;
    private Integer estudianteId;
    private String estudianteNombre;
    private String actividades;
    private Date fechaCreacion;
    private Date fechaInicio;
    private Date fechaFin;
    private Integer tipoContraprestacionId;
    private String tipoContraprestacionNombre;
    private String porcentajeContraprestacion;
    
}
