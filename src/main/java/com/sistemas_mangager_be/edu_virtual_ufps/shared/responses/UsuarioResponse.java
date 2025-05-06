package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponse {

    private Integer id;
    private String nombre;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String cedula;
    private String telefono;
    private String codigo;
    private String rol;
    private String moodleId;
}
