package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linea_investigacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineaInvestigacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_grupo_investigacion", referencedColumnName = "id")
    private GrupoInvestigacion grupoInvestigacion;
}
