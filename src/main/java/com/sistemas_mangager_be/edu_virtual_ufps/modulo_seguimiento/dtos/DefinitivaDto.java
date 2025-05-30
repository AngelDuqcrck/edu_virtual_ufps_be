package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Definitiva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Definitiva}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefinitivaDto implements Serializable {
    Integer id;
    Integer idProyecto;
    Double calificacion;
    Boolean honores;
}