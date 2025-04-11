package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id.MateriaOracleId;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@IdClass(MateriaOracleId.class)
@Table(name = "MATERIA") // Asegúrate que este sea el nombre real de la tabla
public class MateriaOracle {

    @Id
    @Column(name = "COD_DPTO")
    private String codDpto;

    @Id
    @Column(name = "COD_CARRERA")
    private String codCarrera;

    @Id
    @Column(name = "COD_MATERIA")
    private String codMateria;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "HT")
    private Integer ht;

    @Column(name = "HP")
    private Integer hp;

    @Column(name = "CREDITOS")
    private Integer creditos;

    @Column(name = "MULTI_P")
    private String multiP;

    @Column(name = "SEMESTRE")
    private String semestre;

    @Column(name = "ACTIVA")
    private String activa;

    @Column(name = "NBC")
    private String nbc;

    @Column(name = "HTI")
    private Integer hti;

    @Column(name = "HASA")
    private Integer hasa;

    @Column(name = "HASL")
    private Integer hasl;

    @Column(name = "UNICANOTA")
    private String unicanota;

    @Column(name = "MODULO_ACU_012")
    private String moduloAcu012;

    @Column(name = "TIPO_MATERIA")
    private String tipoMateria;

    @Column(name = "ID_MICRO")
    private Integer idMicro;
}
