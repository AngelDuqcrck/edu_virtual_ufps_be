package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;
import java.util.Date;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudResponse {
    
    private Long id;

    private String descripcion;

    private Integer EstudianteId;

    private String estudianteNombre;

    private Date fechaCreacion;

    private String semestre;

    private Long grupoCohorteId;

    private Integer grupoId;

    private String grupoNombre;

    private String grupoCodigo;

    private Integer tipoSolicitudId;

    private String tipoSolicitudNombre;

    private Boolean estaAprobado;

    private String estudianteCodigo;

    private Soporte soporte;
}
