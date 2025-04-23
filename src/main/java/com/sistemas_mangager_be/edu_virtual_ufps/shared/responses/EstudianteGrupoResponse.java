package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;
import java.util.List;

import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteGrupoResponse {
    
    private Long id;
    private String grupoNombre;
    private String grupoCodigo;
    private String grupoCohorte;
    private Integer grupoCohorteId;
    private List<estudianteResponse> estudiantes;
    

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class estudianteResponse{
        private Integer id;
        private String nombre;
        private String codigo;
        private String email;
    }
}
