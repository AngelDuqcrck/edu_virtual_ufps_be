package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotasPregradoDTO {
    
    private Long id;
    private Long grupoCohorteId;
    private String estudianteCodigo;
    private String oracleCodAlumno;
    private Double primerPrevio;
    private Double segundoPrevio;
    private Double terceraNota;
    private Double examenFinal;
    private Double habilitacion;
    private Double notaDefinitiva;
    private Boolean esModificable;
    private String observaciones;
    
    // Campos adicionales para mostrar en UI
    private String nombreEstudiante;
    private String apellidoEstudiante;
    private String documentoEstudiante;
    private String nombreMateria;
}