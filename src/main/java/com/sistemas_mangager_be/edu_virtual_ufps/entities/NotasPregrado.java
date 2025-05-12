package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notas_pregrado")
public class NotasPregrado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relaciona con grupo-cohorte en MySQL (para obtener el moodleId)
    @ManyToOne
    @JoinColumn(name = "grupo_cohorte_id")
    private GrupoCohorte grupoCohorteId;
    
    // Campos para identificar la matrícula en Oracle
    @Column(name = "oracle_cod_alumno")
    private String oracleCodAlumno;
    
    @Column(name = "oracle_cod_carrera")
    private String oracleCodCarrera;
    
    @Column(name = "oracle_cod_materia")
    private String oracleCodMateria;
    
    @Column(name = "oracle_grupo")
    private String oracleGrupo;
    
    @Column(name = "oracle_ciclo")
    private String oracleCiclo;
    
    // Identificación del estudiante en MySQL (referencia, no FK)
    @Column(name = "estudiante_codigo")
    private String estudianteCodigo;
    
    // Calificaciones
    @Column(name = "primer_previo")
    private Double primerPrevio;
    
    @Column(name = "segundo_previo")
    private Double segundoPrevio;
    
    @Column(name = "tercera_nota")
    private Double terceraNota;
    
    @Column(name = "examen_final")
    private Double examenFinal;
    
    @Column(name = "habilitacion")
    private Double habilitacion;
    
    @Column(name = "nota_definitiva")
    private Double notaDefinitiva;
    
    // Campos para integración con Moodle
    @Column(name = "moodle_course_id")
    private String moodleCourseId;
    
    @Column(name = "moodle_student_id")
    private String moodleStudentId;
    
    // Control de estado y sincronización
    @Column(name = "es_modificable")
    private Boolean esModificable;
    
    @Column(name = "moodle_sync_status")
    private Boolean moodleSyncStatus;
    
    @Column(name = "moodle_last_sync")
    @Temporal(TemporalType.TIMESTAMP)
    private Date moodleLastSync;
    
    // Auditoría
    @Column(name = "realizado_por")
    private String realizadoPor;
    
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
    
    @Column(name = "modificado_por")
    private String modificadoPor;
    
    @Column(name = "observaciones")
    private String observaciones;
}