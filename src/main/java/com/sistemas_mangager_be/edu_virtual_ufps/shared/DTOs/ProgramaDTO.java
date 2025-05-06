package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramaDTO {

    private Integer id;

    private String nombre;

    private String codigo;

    private Boolean esPosgrado;

    private Integer moodleId;

}
