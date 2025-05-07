package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrupoCohorteDocenteResponse {

    private Long id;
    private Long grupoCohorteId;
    private Integer grupoId;
    private Integer cohorteGrupoId;
    private Integer docenteId;
    private String docenteNombre;
    private String cohorteGrupoNombre;
    private Integer cohorteId;
    private String cohorteNombre;
    private String fechaCreacion;
    private String grupoNombre;
    private String codigoGrupo;
    private String materia;
    private String codigoMateria;
    private String semestreMateria;
    private String moodleId;
    private Integer programaId;
    private Integer materiaId;
}
