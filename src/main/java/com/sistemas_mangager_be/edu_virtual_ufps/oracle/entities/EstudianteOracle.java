package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

@Table(name = "CONSULTA_ESTUDIANTE")
public class EstudianteOracle {

    @Id
    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "NOMCARRERA")
    private String nomCarrera;

    @Column(name = "PRIMER_NOMBRE")
    private String primerNombre;

    @Column(name = "SEGUNDO_NOMBRE")
    private String segundoNombre;

    @Column(name = "PRIMER_APELLIDO")
    private String primerApellido;

    @Column(name = "SEGUNDO_APELLIDO")
    private String segundoApellido;

    @Column(name = "DOCUMENTO")
    private String documento;

    @Column(name = "TIPO_DOCUMENTO")
    private String tipoDocumento;

    @Column(name = "FECHA_NACIMIENTO")
    private Date fechaNacimiento;

    @Column(name = "TMATRICULADO")
    private String tMatriculado;

    @Column(name = "DESC_TIPOCAR")
    private String descTipoCar;

    @Column(name = "EMAIL")
    private String email;

}