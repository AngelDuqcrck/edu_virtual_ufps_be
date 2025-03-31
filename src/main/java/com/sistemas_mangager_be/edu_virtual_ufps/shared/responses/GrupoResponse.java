package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrupoResponse {
    
    private Integer id;
    private String nombre;
    private Boolean activo;
    private Integer docenteId;
    private String docenteNombre;
    private Integer cohorteId;
    private String cohorteNombre;
    private Integer materiaId;
    private String materiaNombre;
}
