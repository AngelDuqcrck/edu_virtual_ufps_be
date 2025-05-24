package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoSustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionDocumento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionEvaluador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "sustentacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sustentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TipoSustentacion tipoSustentacion;

    private LocalDate fecha;
    private LocalTime hora;
    private LocalTime horaFin;
    private String lugar;
    private String descripcion;
    private Boolean asistenciaConfirmada;
    private Boolean sustentacionExterna;
    private Boolean sustentacionRealizada;

    @ManyToOne
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

    @OneToMany(mappedBy = "sustentacion", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SustentacionEvaluador> evaluadores;

    @OneToMany(mappedBy = "sustentacion", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SustentacionDocumento> documentos;

    @OneToMany(mappedBy = "sustentacion", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CriterioEvaluacion> criteriosEvaluacion;
}

