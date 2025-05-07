package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    private Integer id;

    private String nombre;

    private String primerNombre;

    private String segundoNombre;

    private String primerApellido;

    private String segundoApellido;

    private String email;

    private String googleId;

    private String fotoUrl;

    private Integer rolId;

    private String telefono;

    private String cedula;

    private String codigo;
}
