package com.sistemas_mangager_be.edu_virtual_ufps.entitiesOracle;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "VW_ESTUDIANTES_UFPS")
public class EstudianteVista {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "CORREO")
    private String correo;
}