package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Documento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.ColoquioEstudianteId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coloquio_estudiante")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ColoquioEstudianteId.class)
public class ColoquioEstudiante {

    @Id
    private Integer idColoquio;

    @Id
    private Integer idEstudiante;

    @Id
    private Integer idDocumento;

    @ManyToOne
    @JoinColumn(name = "idColoquio", insertable = false, updatable = false)
    private Coloquio coloquio;

    @ManyToOne
    @JoinColumn(name = "idEstudiante", insertable = false, updatable = false)
    private Usuario estudiante;

    @OneToOne
    @JoinColumn(name = "idDocumento", insertable = false, updatable = false)
    private Documento documento;
}

