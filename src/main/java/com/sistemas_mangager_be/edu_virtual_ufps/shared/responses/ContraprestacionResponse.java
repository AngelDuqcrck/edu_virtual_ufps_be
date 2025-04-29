package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import java.util.Date;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;

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
    private String semestre;
    private Boolean aprobada;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private Soporte soporte;
    
}
