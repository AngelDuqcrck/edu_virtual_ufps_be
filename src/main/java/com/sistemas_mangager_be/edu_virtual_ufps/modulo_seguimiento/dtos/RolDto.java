package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Rol}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDto implements Serializable {
    Integer id;
    String nombre;
}