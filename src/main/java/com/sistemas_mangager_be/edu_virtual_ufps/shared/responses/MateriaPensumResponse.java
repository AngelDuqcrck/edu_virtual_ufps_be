package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MateriaPensumResponse {
    
    public String codigo;
    public String nombre;
    public String creditos;
    public String semestreAprobado; // Semestre en el que se ha aprobado la materia en caso de que se haya aprobado si no devuelve null
    public Integer estadoId;
    public String estadoNombre;
    public String colorCard;
}
