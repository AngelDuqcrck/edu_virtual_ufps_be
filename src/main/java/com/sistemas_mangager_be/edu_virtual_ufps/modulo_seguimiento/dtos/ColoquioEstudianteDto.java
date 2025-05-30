package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.ColoquioEstudiante}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColoquioEstudianteDto implements Serializable {
    private Integer idColoquio;
    private Integer idEstudiante;
    private Integer idDocumento;
}