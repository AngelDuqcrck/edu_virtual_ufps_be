package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;
import java.util.Date;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificadoResponse {
    
    private Integer id;

    private String nombreCompleto;


    private String cedula;

    private String programa;

    private Integer programaId;

    private Integer cohorteId;

    private String cohorteNombre;

    private Date fechaCreacion;

    private String semestre;

    private String actividades;

    private Date fechaInicio;

    private Date fechaFin;

    private Date fechaCertificado;

    private Boolean aprobada;

    private String codigoEstudiante;

    private String tipoContraprestacionNombre;

}
