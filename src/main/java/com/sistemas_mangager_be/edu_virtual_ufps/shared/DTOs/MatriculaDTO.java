package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatriculaDTO {
    
    private Integer id;
    private Integer estadoMatriculaId;
    private Integer estudianteId;
    private Integer grupoId;
}
