package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.GrupoInvestigacion;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ProgramaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link GrupoInvestigacion}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrupoInvestigacionDto implements Serializable {
    Integer id;
    String nombre;
    ProgramaDTO programa;
}