package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ProgramaDTO;

public interface IProgramaService {
    
    ProgramaDTO listarPrograma(Integer id) throws ProgramaNotFoundException;

    ProgramaDTO crearPrograma(ProgramaDTO programaDTO);

    ProgramaDTO actualizarPrograma(ProgramaDTO programaDTO, Integer id) throws ProgramaNotFoundException;

    List<ProgramaDTO> listarProgramas();
}
