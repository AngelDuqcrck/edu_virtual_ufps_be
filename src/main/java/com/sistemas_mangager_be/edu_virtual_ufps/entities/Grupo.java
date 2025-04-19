package com.sistemas_mangager_be.edu_virtual_ufps.entities;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grupos")
public class Grupo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    private String codigo;

    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materiaId;
}
