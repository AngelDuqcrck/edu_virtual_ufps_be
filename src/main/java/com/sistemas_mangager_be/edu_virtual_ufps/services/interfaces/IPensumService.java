package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;


import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.PensumDTO;

public interface IPensumService {
    
    PensumDTO crearPensum(PensumDTO pensumDTO) throws ProgramaNotFoundException;

    PensumDTO listarPensum(Integer id) throws PensumNotFoundException;

    PensumDTO actualizarPensum(PensumDTO pensumDTO, Integer id) throws PensumNotFoundException, ProgramaNotFoundException;

    List<PensumDTO> listarPensums();

    List<PensumDTO> listarPensumsPorPrograma(Integer id) throws ProgramaNotFoundException;
}
