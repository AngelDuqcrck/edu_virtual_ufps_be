package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;
import java.util.Map;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.CohorteDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.CohortePorCarreraDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CohorteResponse;

public interface ICohorteService {
    
    CohorteDTO crearCohorte(CohorteDTO cohorteDTO);

    CohorteResponse listarCohorte(Integer id) throws CohorteNotFoundException;

    CohorteDTO actualizarCohorte(CohorteDTO cohorteDTO, Integer id) throws CohorteNotFoundException;

    public List<CohorteResponse> listarCohortes();
    Map<Programa, List<Cohorte>> listarCohortesPorCarrera();
    
     List<CohortePorCarreraDTO> listarCohortesPorCarreraConGrupos();

}
