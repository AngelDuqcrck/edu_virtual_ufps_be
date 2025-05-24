package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.SustentacionEvaluadorId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sustentacion_evaluador")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(SustentacionEvaluadorId.class)
public class SustentacionEvaluador {
    @Id
    private Integer idSustentacion;

    @Id
    private Integer idUsuario;

    private String observaciones;
    private Double nota;
    boolean juradoExterno;

    @ManyToOne
    @JoinColumn(name = "idSustentacion", insertable = false, updatable = false)
    private Sustentacion sustentacion;

    @ManyToOne
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    private Usuario usuario;
}
