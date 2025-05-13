package com.sistemas_mangager_be.edu_virtual_ufps.entities;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor 
@NoArgsConstructor
@Table(name = "pensums")
public class Pensum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "programa_id")
    private Programa programaId;
    
    private Integer cantidadSemestres;

    private String moodleId;

}