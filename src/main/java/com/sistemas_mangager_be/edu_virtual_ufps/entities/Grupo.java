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

    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Usuario docenteId;

    @ManyToOne
    @JoinColumn(name = "cohorte_id")
    private Cohorte cohorteId;

    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materiaId;
}
