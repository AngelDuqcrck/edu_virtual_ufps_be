package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "matriculas")
public class Matricula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estado_matricula_id")
    private EstadoMatricula estadoMatriculaId;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudianteId;

    private Date fechaMatriculacion;

    private boolean nuevaMatricula; //Lo usamos para controlar si la matricula es nueva o no en el formulario de registro de matriculas  si es una matricula antigua se pedira la fecha aproximada de la matriculo

    private Double nota;

    private Date fechaNota;
    
    @ManyToOne
    @JoinColumn(name = "grupo_cohorte_id")
    private GrupoCohorte grupoCohorteId;


    private String semestre; //Con la fecha de matriculacion se calcula el semestre de la matricula primer o segundo semestre de X año
    

    private boolean correoEnviado;

    private Date fechaCorreoEnviado;

    
    private Boolean notaAbierta;

    
}
