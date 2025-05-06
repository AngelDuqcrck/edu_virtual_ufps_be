package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import java.util.Date;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CambioEstadoMatriculaResponse {
    
    private Long id;

    private Integer estadoMatriculaId;
    private String estadoMatriculaNombre;

    private Integer materiaId;
    private String materiaNombre;
    private String materiaCodigo;

    private Integer grupoId;
    private String grupoNombre;
    private String grupoCodigo;

    private Date fechaCambioEstadoMatricula;
    private String usuarioCambioEstadoMatricula;
}
