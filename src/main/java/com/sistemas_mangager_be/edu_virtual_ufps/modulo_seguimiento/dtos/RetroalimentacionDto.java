package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Retroalimentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoSustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Retroalimentacion}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetroalimentacionDto implements Serializable {
    private Integer id;
    private String descripcion;

    private Integer usuarioId;
    private Integer documentoId;

    private String nombreUsuario;
    private String fotoUsuario;
}