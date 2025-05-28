package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaseDto {
    private int numeroFaseActual;
    private String descripcionFaseActual;
    private int totalFases;
    private int porcentajeCompletado;
    private List<String> fasesCompletadas;
    private List<String> fasesPendientes;
}
