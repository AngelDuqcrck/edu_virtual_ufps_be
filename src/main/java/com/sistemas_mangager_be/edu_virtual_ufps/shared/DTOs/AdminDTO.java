package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDTO {
    
    private String primerNombre;

    private String segundoNombre;

    private String primerApellido;

    private String segundoApellido;
    
    private String email;
    
    private String password;

    private Boolean esSuperAdmin;
}
