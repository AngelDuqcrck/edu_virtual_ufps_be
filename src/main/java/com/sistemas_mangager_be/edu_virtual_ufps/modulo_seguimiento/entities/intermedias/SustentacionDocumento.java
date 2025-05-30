package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Documento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.SustentacionDocumentoId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sustentacion_documento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(SustentacionDocumentoId.class)
public class SustentacionDocumento {
    @Id
    private Integer idSustentacion;

    @Id
    private Integer idDocumento;

    @ManyToOne
    @JoinColumn(name = "idSustentacion", insertable = false, updatable = false)
    private Sustentacion sustentacion;

    @ManyToOne
    @JoinColumn(name = "idDocumento", insertable = false, updatable = false)
    private Documento documento;
}

