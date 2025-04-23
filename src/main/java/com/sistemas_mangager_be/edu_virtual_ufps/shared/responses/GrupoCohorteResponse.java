package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrupoCohorteResponse {
    
    private Long id;

    private String grupoNombre;

    private Integer grupoId;

    private String grupoCodigo;

    private Integer cohorteGrupoId;

    private String cohorteGrupoNombre;

    private Integer cohorteId;

    private String cohorteNombre;

}
