package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "definitiva")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Definitiva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_proyecto", referencedColumnName = "id")
    private Proyecto proyecto;

    private Double calificacion;

    private Boolean honores;
}

