package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "estudiantes", schema = "sys")
public class EstudianteOracle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    private String documento;

    
}