package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import java.util.Date;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudDTO {
    
    private Long id;

    private String descripcion;

    private Integer estudianteId;

    private Long matriculaId; //Solo para tipo de solicitud de cancelacion de materias

}
