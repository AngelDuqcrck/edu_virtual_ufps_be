package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "objetivo_especifico")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjetivoEspecifico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

    private Integer numeroOrden;

    private String descripcion;

    private Integer avanceReportado;

    private Integer avanceReal;

    private Boolean evaluacion;

    private LocalDate fecha_inicio;

    private LocalDate fecha_fin;
}

