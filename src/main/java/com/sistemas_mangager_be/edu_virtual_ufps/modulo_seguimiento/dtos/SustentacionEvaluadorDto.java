package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionEvaluador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link SustentacionEvaluador}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SustentacionEvaluadorDto implements Serializable {
    private Integer idSustentacion;
    private Integer idUsuario;
    private String observaciones;
    private Double nota;
    boolean juradoExterno;

    private String nombreUsuario;
    private String fotoUsuario;
    private String email;
    private String telefono;
}