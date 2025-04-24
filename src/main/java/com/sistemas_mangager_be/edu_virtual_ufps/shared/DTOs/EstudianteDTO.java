package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import java.sql.Date;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteDTO {
    
    private Integer id;

    private String codigo;

    private String nombre;

    private String nombre2;

    private String apellido;

    private String apellido2;

    private String email;

    private String cedula;
    
    private String telefono;

    private Date fechaNacimiento;

    private Date fechaIngreso;

    private Boolean esPosgrado;

    private Integer pensumId;

    private Integer programaId;

    private Integer cohorteId;

    private Integer estadoEstudianteId;

    private Integer usuarioId;
  
}
