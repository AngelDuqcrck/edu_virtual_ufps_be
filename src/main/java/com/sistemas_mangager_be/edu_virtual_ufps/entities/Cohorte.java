package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cohortes")
public class Cohorte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    private Date fechaCreación;
}
