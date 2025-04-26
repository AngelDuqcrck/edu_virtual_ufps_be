package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import java.util.Date;

import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstudianteMigradoDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String nombre2;
    private String apellido;
    private String apellido2;
    private String moodleId;
    private String email;
    private String telefono;
    private String cedula;
    private Date fechaNacimiento;
    private Date fechaIngreso;
    private Boolean esPosgrado;
    private Integer pensumId;
    private Integer programaId;
    private Integer cohorteId;
    private Integer estadoEstudianteId;
    private Integer usuarioId;
    private Boolean migrado;
}
