package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    private String primerNombre;

    private String segundoNombre;


    private String primerApellido;

    private String segundoApellido;
    
    private String email;
    
    private String password;

    private Integer rolId;
}
