package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoDocumento;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Documento}
 */
@Value
public class DocumentoDto implements Serializable {
    Integer id;
    String tipoArchivo;
    String nombre;
    String path;
    String peso;
    TipoDocumento tipoDocumento;
    Integer idProyecto;
}