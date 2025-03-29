package com.sistemas_mangager_be.edu_virtual_ufps.shared.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocenteRequest {
    private String nombre;
    
    private String email;

    private String telefono;

    private Integer rolId;

    private String cedula;

    private String codigo;
}
