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
    private String CODIGO;

    private String NOMCARRERA;
    private String PRIMER_NOMBRE;
    private String SEGUNDO_NOMBRE;
    private String PRIMER_APELLIDO;
    private String SEGUNDO_APELLIDO;
    private String DOCUMENTO;
    private String TIPO_DOCUMENTO;
    private Date FECHA_NACIMIENTO;
    private String TMATRICULADO;
    private String DESC_TIPOCAR;
    private String EMAIL;

}