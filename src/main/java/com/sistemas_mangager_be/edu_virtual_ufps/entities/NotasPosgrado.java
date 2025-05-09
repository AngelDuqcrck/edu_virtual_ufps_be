package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;

import java.util.Date;

import jakarta.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NotasPosgrado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Double nota;

    private Date fechaNota;

    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matriculaId;

    private String realizadoPor;

}
