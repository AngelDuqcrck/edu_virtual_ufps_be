package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link SustentacionDocumento}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SustentacionDocumentoDto implements Serializable {
    private Integer idSustentacion;
    private Integer idDocumento;
}