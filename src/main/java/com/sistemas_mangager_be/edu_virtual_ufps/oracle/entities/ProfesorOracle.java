package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

@Table(name = "CONSULTA_PROFESOR")
public class ProfesorOracle {
    @Id
    @Column(name = "COD_PROFESOR")
    private String codProfesor;

    @Column(name = "DOCUMENTO")
    private String documento;

    @Column(name = "TIPO_DOCUMENTO")
    private String tipoDocumento;

    @Column(name = "FECHA_NACIMIENTO")
    private Date fechaNacimiento;

    @Column(name = "NOMBRE1")
    private String nombre1;

    @Column(name = "NOMBRE2")
    private String nombre2;

    @Column(name = "APELLIDO1")
    private String apellido1;

    @Column(name = "APELLIDO2")
    private String apellido2;

    @Column(name = "EMAILI")
    private String emaili;
}
