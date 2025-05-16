package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PensumSemestreResponse {

    private Integer id;
    private String nombre;
    private Integer cantidadSemestres;
    private Integer programaId;
    private String programaNombre;
    private String moodleId;
    private List<SemestreResponse> semestres;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SemestreResponse {
        private Integer id;
        private String nombre;
        private Integer numero;
        private String moodleId;
    }
}
