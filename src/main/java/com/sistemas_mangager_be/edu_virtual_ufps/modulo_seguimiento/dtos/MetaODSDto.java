package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.MetaODS}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaODSDto implements Serializable {
    private Integer id;
    private String nombre;
}