package com.sistemas_mangager_be.edu_virtual_ufps.entities;
import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historial_cierre_notas")
public class HistorialCierreNotas {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "grupo_cohorte_id", nullable = false)
    private Long grupoCohorteId;
    
    @Column(name = "matricula_id", nullable = false)
    private Long matriculaId;
    
    @Column(name = "fecha_cierre", nullable = false)
    private Date fechaCierre;
    
    @Column(name = "realizado_por", nullable = false, length = 100)
    private String realizadoPor;
}
