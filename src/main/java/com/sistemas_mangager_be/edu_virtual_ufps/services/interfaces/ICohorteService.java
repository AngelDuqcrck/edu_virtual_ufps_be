package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.CohorteDTO;

public interface ICohorteService {
    
    CohorteDTO crearCohorte(CohorteDTO cohorteDTO);

    CohorteDTO listarCohorte (Integer id) throws CohorteNotFoundException;

    CohorteDTO actualizarCohorte(CohorteDTO cohorteDTO, Integer id) throws CohorteNotFoundException;

    List<CohorteDTO> listarCohortes();


}
