package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipo_solicitud_id")
    private TipoSolicitud tipoSolicitudId;
    
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudianteId;

    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matriculaId;

    private Date fechaCreacion;

    private Date fechaAprobacion;

    private Boolean estaAprobada;

    @ManyToOne
    @JoinColumn(name = "soporte_id")
    private Soporte soporteId;


}
