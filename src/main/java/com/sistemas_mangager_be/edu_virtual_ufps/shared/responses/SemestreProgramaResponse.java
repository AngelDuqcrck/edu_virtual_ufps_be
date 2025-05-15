package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SemestreProgramaResponse {

    private Integer id;
    private String nombre;
    private String codigo;
    private List<SemestreResponse> semestres;
    private String moodleId;
    private Boolean esPosgrado;
    
     @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SemestreResponse{
        private Integer id;
        private String nombre;
        private Integer numero;
        private String moodleId; 
    }

}
