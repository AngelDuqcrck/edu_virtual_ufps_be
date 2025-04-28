package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;
import java.util.Date;
import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CohorteResponse {
    
    
    private Integer id;
    private String nombre;
    private Date fechaCreacion;
    private List<CohortesGrupos> cohortesGrupos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CohortesGrupos {
    
        private Integer id;
        private String nombre;
        

    }

}
