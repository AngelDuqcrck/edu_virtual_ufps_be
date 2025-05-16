package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MateriaDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String creditos;
    private String semestre;
    private Integer pensumId;
    private Integer semestrePensumId;
    
}
