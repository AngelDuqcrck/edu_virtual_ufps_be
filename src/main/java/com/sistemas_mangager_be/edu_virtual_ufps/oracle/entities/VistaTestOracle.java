package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities;
import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "vista_test", schema = "sys")
public class VistaTestOracle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
}
