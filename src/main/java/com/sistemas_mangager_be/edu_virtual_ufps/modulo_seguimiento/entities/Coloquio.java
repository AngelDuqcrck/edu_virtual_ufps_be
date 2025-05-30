package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "coloquio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coloquio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fecha;
    private LocalTime hora;
    private String lugar;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "grupo_cohorte_id")
    private GrupoCohorte grupoCohorte;
}

