package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import java.sql.Date;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContraprestacionDTO {
    private Integer id;
    private Integer estudianteId;
    private String actividades;
    private boolean activa;
    private Date fechaCreacion;
    private Date fechaInicio;
    private Date fechaFin;
    private Integer tipoContraprestacionId;
    private Integer soporteId;
    
}
