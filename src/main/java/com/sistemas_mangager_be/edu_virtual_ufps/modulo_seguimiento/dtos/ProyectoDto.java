package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Proyecto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProyectoDto implements Serializable {
    Integer id;
    String titulo;
    String pregunta;
    String problema;
    String objetivoGeneral;
    Integer estadoActual;
    String estadoDescripcion;
    List<ObjetivoEspecificoDto> objetivosEspecificos;
    LineaInvestigacionDto lineaInvestigacion;
    List<UsuarioProyectoDto> usuariosAsignados;
    LocalDate createdAt;
    LocalDate updatedAt;
    DefinitivaDto definitiva;
}