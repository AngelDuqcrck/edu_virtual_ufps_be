package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    private String nombre;
    
    private String email;
    
    private String googleId;

    private String fotoUrl;

    private Integer rolId;
}
