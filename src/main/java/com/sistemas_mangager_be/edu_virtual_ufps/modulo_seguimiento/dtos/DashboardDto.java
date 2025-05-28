package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDto {
    private FaseDto faseActual;
    private List<ActividadDto> proximaActividad;
    private List<ActividadDto> tareasAtrasadas;
}
