package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "docente", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "codigo"})})
public class Docente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    private String apellido;

    @NotEmpty
    private String email;

    private String telefono;

    @NotEmpty
    private String codigo;

}
