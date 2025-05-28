package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ProgramaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GruposYLineasInvestigacionDto implements Serializable {
    Integer id;
    String nombre;
    ProgramaDTO programa;

    List<LineaInvestigacionBasicaDto> lineasInvestigacion;
}
