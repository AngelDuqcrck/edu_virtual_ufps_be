package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColoquioEstudianteId implements Serializable {
    private Integer idColoquio;
    private Integer idEstudiante;
    private Integer idDocumento;
}

