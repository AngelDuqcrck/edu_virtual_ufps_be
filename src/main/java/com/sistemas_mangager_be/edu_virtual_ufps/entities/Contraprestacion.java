package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contraprestaciones")
public class Contraprestacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudianteId;

    private String actividades;

    private Boolean activa;

    private Date fechaInicio;

    private Date fechaFin;

    @ManyToOne
    @JoinColumn(name = "tipo_contraprestacion_id")
    private TipoContraprestacion tipoContraprestacionId;

    @ManyToOne
    @JoinColumn(name = "soporte_id")
    private Soporte soporteId;

}
