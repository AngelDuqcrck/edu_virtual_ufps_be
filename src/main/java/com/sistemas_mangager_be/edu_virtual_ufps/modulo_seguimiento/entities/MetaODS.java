package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meta_ods")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaODS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
}
