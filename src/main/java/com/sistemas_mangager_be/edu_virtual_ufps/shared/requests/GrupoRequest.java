package com.sistemas_mangager_be.edu_virtual_ufps.shared.requests;
import java.util.Date;

import lombok.*;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrupoRequest {
    
    private Integer grupoId;

    private Integer cohorteGrupoId;

    private Integer docenteId;

    private Date fechaCreacion;
}
