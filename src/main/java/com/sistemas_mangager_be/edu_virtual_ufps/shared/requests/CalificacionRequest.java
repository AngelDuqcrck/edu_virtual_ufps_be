package com.sistemas_mangager_be.edu_virtual_ufps.shared.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalificacionRequest {
    
    private Double calificacion;
    private Integer matriculaId;

}
