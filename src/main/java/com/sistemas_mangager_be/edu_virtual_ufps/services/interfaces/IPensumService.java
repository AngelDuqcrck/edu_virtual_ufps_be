package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;


import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.PensumDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;

public interface IPensumService {
    
    PensumDTO crearPensum(PensumDTO pensumDTO) throws ProgramaNotFoundException;

    public void vincularMoodleId(MoodleRequest moodleRequest)throws PensumNotFoundException, PensumExistException;
    PensumDTO listarPensum(Integer id) throws PensumNotFoundException;

    PensumDTO actualizarPensum(PensumDTO pensumDTO, Integer id) throws PensumNotFoundException, ProgramaNotFoundException;

    List<PensumDTO> listarPensums();

    List<PensumDTO> listarPensumsPorPrograma(Integer id) throws ProgramaNotFoundException;
}
