package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import java.util.Date;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudDTO {
    
    private Long id;

    private Integer tipoSolicitudId;

    private String tipoSolictudNombre;

    private String estudianteId;

    private String matriculaId; //Solo para tipo de solicitud de cancelacion de materias

    private Date fechaCreacion;

    private Date fechaAprobacion;

    private boolean estaAprobada;

    private Integer soporteId;

}
