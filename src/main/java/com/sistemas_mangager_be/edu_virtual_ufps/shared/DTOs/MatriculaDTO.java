package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatriculaDTO {

    private Integer id;
    private Integer estudianteId;
    private Long grupoCohorteId;
    private boolean nuevaMatricula;
    private Date fechaMatriculacion;

}
