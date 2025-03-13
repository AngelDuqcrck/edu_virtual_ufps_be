package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
}
